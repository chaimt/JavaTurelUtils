package com.turel.utils.random;

import com.turel.utils.reflection.ReflectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.*;

/**
 * This class will generate values on all fields in class. It supports recursive
 * objects. In the case of a list you can specify the number of elements in list
 * In the case of a cycle you can specify the number of cycles allowed
 *
 * Created by Chaim on 08/05/2017.
 */
public class ClassGeneratorHelper {

    private static final Logger logger = LoggerFactory.getLogger(ClassGeneratorHelper.class);
    private Stack<CycleStack<Class<?>>> typesCreatedStack = new Stack<>();
    private int listCountRepition = 1;
    private int cycleCountRepition = 1;
    private _BaseRandomField baseRandomField;
    private boolean recursive = true;
    private ParameterizedType keyParameterizedType;

    public ClassGeneratorHelper() {

    }

    public ClassGeneratorHelper(int listCountRepition, int cycleCountRepition, _BaseRandomField baseRandomField) {
        super();
        this.listCountRepition = listCountRepition;
        this.cycleCountRepition = cycleCountRepition;
        this.baseRandomField = baseRandomField;
    }

    public ClassGeneratorHelper(int listCountRepition, int cycleCountRepition, _BaseRandomField baseRandomField,
                                boolean recursive) {
        super();
        this.listCountRepition = listCountRepition;
        this.cycleCountRepition = cycleCountRepition;
        this.baseRandomField = baseRandomField;
        this.recursive = recursive;
    }

    public ClassGeneratorHelper(int listCountRepition, int cycleCountRepition) {
        super();
        this.listCountRepition = listCountRepition;
        this.cycleCountRepition = cycleCountRepition;
    }

    public int getListCountRepition() {
        return listCountRepition;
    }

    public void setListCountRepition(int listCountRepition) {
        this.listCountRepition = listCountRepition;
    }

    public boolean isRecursive() {
        return recursive;
    }

    public void setRecursive(boolean recursive) {
        this.recursive = recursive;
    }

    public _BaseRandomField getBaseRandomField() {
        return baseRandomField;
    }

    public void registerBaseRandomField(_BaseRandomField baseRandomField) {
        this.baseRandomField = baseRandomField;
    }

    private Object generateRandomFieldValuesForBaseClass(Class<?> type, Random random) {
        try {
            if (baseRandomField != null) {
                Object fieldValue = baseRandomField.generateRandom(type, random);
                if (fieldValue != null) {
                    return fieldValue;
                }
            }

            if (type == Byte.class || type == byte.class) {
                byte[] bytes = new byte[1];
                random.nextBytes(bytes);
                return bytes[0];
            } else if (type == char.class) {
                byte[] bytes = new byte[1];
                random.nextBytes(bytes);
                return (char) bytes[0];
            } else if (type == Short.class || type == short.class) {
                return (short) random.nextInt(Short.MAX_VALUE);
            } else if (type == Integer.class || type == int.class) {
                return random.nextInt(Integer.MAX_VALUE) + 1;
            } else if (type == Long.class || type == long.class) {
                return random.nextLong() + 1;
            } else if (type == Double.class || type == double.class) {
                return random.nextDouble();
            } else if (type == Float.class || type == float.class) {
                return random.nextFloat();
            } else if (type == Date.class) {
                return new Date();
            } else if (type == String.class) {
                return (new BigInteger(130, random)).toString(32);
            } else if (type == Boolean.class || type == boolean.class) {
                return random.nextBoolean();
            } else if (type == BigDecimal.class) {
                return new BigDecimal(random.nextInt(Integer.MAX_VALUE) + 1);
            } else if (type == BigInteger.class) {
                return new BigInteger(130, random);
            } else if (type == Timestamp.class) {
                return new Timestamp(random.nextLong() + 1);
            } else if (type.isEnum()) {
                return EnumHelper.getRandomValue((Class<Enum>) type);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    private boolean reachedCycle(CycleStack<Class<?>> currentStack, Class<?> type) {
        if (currentStack.indexOf(type) != -1) {
            // we have found a cycle
            if (currentStack.getCycleDepth() == cycleCountRepition) {
                return true;
            }
            Stack<Class<?>> newStack = currentStack.newStack();
            newStack.push(type);
        }
        return false;
    }

    private CycleStack<Class<?>> getCurrentStack() {
        CycleStack<Class<?>> peek = typesCreatedStack.size() > 0 ? typesCreatedStack.peek() : null;
        if (peek == null) {
            peek = new CycleStack<>();
            typesCreatedStack.push(peek);
        }
        return peek;
    }

    private void popFromCurrentStack(CycleStack<Class<?>> peek) {
        peek.pop();
        if (peek.size() == 0) {
            typesCreatedStack.pop();
        }

    }

    private Constructor<?> getDefaultConstructor(Class<?> type) {
        for (Constructor<?> constructor : type.getConstructors()) {
            int length = constructor.getParameterTypes().length;
            if (length == 0) {
                return constructor;
            }
        }
        return null;
    }

    private Object createFromFirstConstructor(Class<?> type) {
        try {
            if (type.getConstructors().length == 0) {
                return null;
            }
            Constructor<?> constructor = type.getConstructors()[0];
            final List<Object> params = new ArrayList<Object>();
            for (Class<?> pType : constructor.getParameterTypes()) {
                params.add((pType.isPrimitive()) ? ReflectionUtils.newInstanceForPrimitive(pType) : null);
            }
            return constructor.newInstance(params.toArray());
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            logger.error(String.format("error creating object %s: %s [%s]", type.getCanonicalName(), e.getClass()
                    .getCanonicalName(), e.getMessage()));
            return null;
        }
    }

    private Object createObject(Class<?> type) {
        if (Modifier.isAbstract(type.getModifiers())) {
            logger.info("Abstract Type Found: " + type.getName());
        }

        else if (type.isInterface()) {
            logger.info("Interface Type Found: " + type.getName());
        } else {
            try {
                Constructor<?> ctor = getDefaultConstructor(type);
                if (ctor == null) {
                    Object inst = createFromFirstConstructor(type);
                    if (inst == null) {
                        logger.info("No Constructor For Type Found: " + type.getName());
                    }
                    return inst;
                } else {
                    return ctor.newInstance();
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }

    private void generateForCollection(Object object, Field field, Collection collection, CycleStack<Class<?>> peek,
                                       Type genericType, Random random) throws NoSuchMethodException, SecurityException, InstantiationException,
            IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        if (genericType instanceof ParameterizedType) {
            Type type = ((ParameterizedType) genericType).getActualTypeArguments()[0];
            if (type instanceof Class) {
                Class<?> clazz = (Class<?>) ((ParameterizedType) genericType).getActualTypeArguments()[0];
                if (reachedCycle(peek, clazz)) {
                    return;
                }
                peek = getCurrentStack();
                for (int count = 0; count < listCountRepition; count++) {
                    Object externalObject = generateRandomFieldValuesForBaseClass(clazz, random);
                    if (externalObject == null) {
                        externalObject = createObject(clazz);
                    }
                    if (externalObject != null) {
                        collection.add(externalObject);
                        generateRandomFieldValues(externalObject);
                    }
                }
            } else {
                logger.error(String.format("Complex Collection field not supported %s.%s", object.getClass().getName(), field.getName()));
            }
        } else {
            logger.error(String.format("Collection field has no generics %s.%s", object.getClass().getName(),
                    field.getName()));
        }
    }

    private void generateForMap(Object object, Field field, Map map, CycleStack<Class<?>> peek, Type genericType,
                                Random random) throws NoSuchMethodException, SecurityException, InstantiationException,
            IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        if (genericType != null && genericType instanceof ParameterizedType) {
            Type keyParameterizedType = ((ParameterizedType) genericType).getActualTypeArguments()[0];
            Type valueParameterizedType = ((ParameterizedType) genericType).getActualTypeArguments()[1];
            if (keyParameterizedType instanceof Class && valueParameterizedType instanceof Class) {
                Class<?> keyClazz = (Class<?>) keyParameterizedType;
                Class<?> valueClazz = (Class<?>) valueParameterizedType;
                if (reachedCycle(peek, valueClazz)) {
                    return;
                }
                peek = getCurrentStack();
                for (int count = 0; count < listCountRepition; count++) {
                    Object externalObject = generateRandomFieldValuesForBaseClass(valueClazz, random);
                    if (externalObject == null) {
                        externalObject = createObject(valueClazz);
                    }
                    if (externalObject != null) {
                        Object keyObject = generateRandomFieldValuesForBaseClass(keyClazz, random);
                        if (keyObject == null) {
                            keyObject = createObject(keyClazz);
                            // generateRandomFieldValues(keyObject);
                        }
                        map.put(keyObject, externalObject);
                        generateRandomFieldValues(externalObject);
                    }
                }
            }
            else{
                logger.error(String.format("Complex Map field not supported %s.%s", object.getClass().getName(), field.getName()));
            }
        } else {
            logger.error(String.format("Map field has no generics %s.%s", object.getClass().getName(), field.getName()));
        }

    }

    private void generateForArray(Object array, Class<?> clazz, CycleStack<Class<?>> peek, Random random)
            throws NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException {
        if (reachedCycle(peek, clazz)) {
            return;
        }
        peek = getCurrentStack();

        for (int count = 0; count < listCountRepition; count++) {
            Object externalObject = generateRandomFieldValuesForBaseClass(clazz, random);
            if (externalObject == null) {
                externalObject = createObject(clazz);
            }
            if (externalObject != null) {
                Array.set(array, count, externalObject);
                generateRandomFieldValues(externalObject);
            }
        }
    }


    public void generateRandomFieldValues(Object object) {
        logger.debug("generateRandomFieldValues: " + object.getClass().getSimpleName());
        CycleStack<Class<?>> peek = getCurrentStack();
        peek.push(object.getClass());
        try {
            Random random = new Random();
            Field[] allDeclaredFields = ReflectionUtils.getAllDeclaredFields(object.getClass());
            for (Field field : allDeclaredFields) {
                try {
                    if (reachedCycle(peek, field.getType())) {
                        return;
                    }
                    peek = getCurrentStack();
                    field.setAccessible(true);
                    Class<?> type = field.getType();
                    Object createdClass = generateRandomFieldValuesForBaseClass(field.getType(), random);
                    if (createdClass != null) {
                        field.set(object, createdClass);
                    } else if (recursive) {
                        if (type.isArray()) {
                            Object array = Array.newInstance(type.getComponentType(), listCountRepition);
                            field.set(object, array);
                            generateForArray(array, type.getComponentType(), peek, random);
                        } else if (type.isAssignableFrom(HashMap.class)) {
                            Map<Object, Object> map = new HashMap<Object, Object>();
                            field.set(object, map);
                            generateForMap(object, field, map, peek, field.getGenericType(), random);
                        } else if (type.isAssignableFrom(Set.class)) {
                            Set<Object> set = new HashSet<Object>();
                            generateForCollection(object, field, set, peek, field.getGenericType(), random);
                            field.set(object, set);
                        } else if (type.isAssignableFrom(List.class)) {
                            List<Object> list = new ArrayList<Object>();
                            generateForCollection(object, field, list, peek, field.getGenericType(), random);
                            field.set(object, list);
                        } else {
                            Object externalObject = createObject(type);
                            if (externalObject != null) {
                                field.set(object, externalObject);
                                generateRandomFieldValues(externalObject);
                            }
                        }
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        } finally {
            popFromCurrentStack(peek);
        }
    }
}

