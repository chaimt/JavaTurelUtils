package com.turel.utils.random;

import com.turel.utils.random.testClasses.*;
import org.junit.Assert;
import org.junit.Test;

public class TestClassGeneratorHelper {

	@Test
	public void testHashMap(){
		ClassGeneratorHelper classGeneratorHelper = new ClassGeneratorHelper();
		HashWrapper hashWrapper = new HashWrapper();
		classGeneratorHelper.generateRandomFieldValues(hashWrapper);
		Assert.assertNotNull(hashWrapper.getHashMap());
		Assert.assertEquals(1, hashWrapper.getHashMap().keySet().size());
		Assert.assertNotNull(hashWrapper.getNoGenericsHashMap());
		Assert.assertEquals(0, hashWrapper.getNoGenericsHashMap().keySet().size());
	}
	
	@Test
	public void testArray(){
		ClassGeneratorHelper classGeneratorHelper = new ClassGeneratorHelper();
		ArrayWrapper arrayWrapper = new ArrayWrapper();
		classGeneratorHelper.generateRandomFieldValues(arrayWrapper);
		Assert.assertNotNull(arrayWrapper.getA());
		Assert.assertEquals(1, arrayWrapper.getA().length);
		Assert.assertNotNull(arrayWrapper.getB());
		Assert.assertEquals(1, arrayWrapper.getB().length);
		Assert.assertNotNull(arrayWrapper.getC());
		Assert.assertEquals(1, arrayWrapper.getC().length);
		ValueClass[] c = arrayWrapper.getC();
		Assert.assertNotNull(c[0].getY());
		
		classGeneratorHelper.setListCountRepition(2);
		classGeneratorHelper.generateRandomFieldValues(arrayWrapper);
		Assert.assertNotNull(arrayWrapper.getA());
		Assert.assertEquals(2, arrayWrapper.getA().length);
		Assert.assertNotNull(arrayWrapper.getB());
		Assert.assertEquals(2, arrayWrapper.getB().length);
		Assert.assertNotNull(arrayWrapper.getC());
		Assert.assertEquals(2, arrayWrapper.getC().length);
		c = arrayWrapper.getC();
		Assert.assertNotNull(c[0].getY());
		Assert.assertNotNull(c[1].getY());
	}
	
	
	@Test
	public void testList(){
		ClassGeneratorHelper classGeneratorHelper = new ClassGeneratorHelper();
		ListWrapper listWrapper = new ListWrapper();
		classGeneratorHelper.generateRandomFieldValues(listWrapper);
		Assert.assertNotNull(listWrapper.getA());
		Assert.assertEquals(1, listWrapper.getA().size());
		
		Assert.assertNotNull(listWrapper.getB());
		Assert.assertEquals(0, listWrapper.getB().size());
		
		classGeneratorHelper.setListCountRepition(2);
		listWrapper = new ListWrapper();
		classGeneratorHelper.generateRandomFieldValues(listWrapper);
		Assert.assertNotNull(listWrapper.getA());
		Assert.assertEquals(2, listWrapper.getA().size());
		
		Assert.assertNotNull(listWrapper.getB());
		Assert.assertEquals(0, listWrapper.getB().size());
	}
	
	@Test
	public void testSet(){
		ClassGeneratorHelper classGeneratorHelper = new ClassGeneratorHelper();
		SetWrapper setWrapper = new SetWrapper();
		classGeneratorHelper.generateRandomFieldValues(setWrapper);
		Assert.assertNotNull(setWrapper.getMySet());
		Assert.assertEquals(1, setWrapper.getMySet().size());
		
		Assert.assertNotNull(setWrapper.getMySetNoGenerics());
		Assert.assertEquals(0, setWrapper.getMySetNoGenerics().size());
		
		classGeneratorHelper.setListCountRepition(2);
		setWrapper = new SetWrapper();
		classGeneratorHelper.generateRandomFieldValues(setWrapper);
		Assert.assertNotNull(setWrapper.getMySet());
		Assert.assertEquals(2, setWrapper.getMySet().size());
		
		Assert.assertNotNull(setWrapper.getMySetNoGenerics());
		Assert.assertEquals(0, setWrapper.getMySetNoGenerics().size());
	}
	
	
}
