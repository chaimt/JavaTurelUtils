
function testInputInternal(inParam){
    return inParam;
}

/**
 * get array of months include start, exclude end
 * use 10 of month to avoid end/start month issues
 * @param fromBQDate
 * @param toBQDate
 * @returns {Array}
 */
function getBetweenMonthsLeftInclusiveInternal(fromBQDate , toBQDate){
    var months = [];
    var bqfromDate = new Date(fromBQDate);
    var bqtoDate = new Date(toBQDate);
    bqfromDate = new Date(bqfromDate.getFullYear(), bqfromDate.getMonth(), 10);
    bqtoDate = getEndMonthNormalized(bqtoDate,10);
    if (bqfromDate <= bqtoDate) {
        var i=0;
        for (var m = bqfromDate; m < bqtoDate; m.setMonth(m.getMonth() + 1)) {
            months[i] = m.getMonth()+1;
            i++;
        }
    }
    return months;
}

/**
* get array of months exclude start, include end
* @param fromBQDate
* @param toBQDate
* @returns {Array}
*/
function getBetweenMonthsRightInclusiveInternal(fromBQDate , toBQDate){
    var months = [];
    var bqfromDate = new Date(fromBQDate);
    var bqtoDate = new Date(toBQDate);
    bqfromDate = new Date(bqfromDate.getFullYear(), bqfromDate.getMonth()+1, 10);
    bqtoDate = getEndMonthNormalized(bqtoDate,11);
    if (bqfromDate <= bqtoDate) {
        var i=0;
        for (var m = bqfromDate; m <= bqtoDate; m.setMonth(m.getMonth() + 1)) {
            months[i] = m.getMonth()+1;
            i++;
        }
    }
    return months;
}

/**
* get array of months include start, include end
* @param fromBQDate
* @param toBQDate
* @returns {Array}
*/
function getBetweenMonthsInclusiveInternal(fromBQDate , toBQDate){
    var months = [];
    var bqfromDate = new Date(fromBQDate);
    var bqtoDate = new Date(toBQDate);
    bqfromDate = new Date(bqfromDate.getFullYear(), bqfromDate.getMonth(), 10);
    bqtoDate = getEndMonthNormalized(bqtoDate,11);
    if (bqfromDate <= bqtoDate) {
        var i=0;
        for (var m = bqfromDate; m <= bqtoDate; m.setMonth(m.getMonth() + 1)) {
            months[i] = m.getMonth()+1;
            i++;
        }
    }
    return months;

}

/**
 * date that do not have and end date will use the const of 2999. In this case the end data will be the current date
 * @param bqtoDate
 * @param day
 * @returns {*}
 */
function getEndMonthNormalized(bqtoDate,day){
    if (bqtoDate.getFullYear()>2990)
        bqtoDate = new Date();
    else
        bqtoDate = new Date(bqtoDate.getFullYear(), bqtoDate.getMonth(),day);
    return bqtoDate;
}

/**
 * get an array of all dates of end of month from the first to last date
 * @param fromBQDate
 * @param toBQDate
 * @returns {Array}
 */
function getEndMonthsLeftInclusiveInternal(fromBQDate , toBQDate){
    var months = [];
    var bqfromDate = new Date(fromBQDate);
    var bqtoDate = new Date(toBQDate);
    bqfromDate = new Date(bqfromDate.getFullYear(), bqfromDate.getMonth(), 10);
    bqtoDate = getEndMonthNormalized(bqtoDate,10);
    if (bqfromDate <= bqtoDate) {
        var i=0;
        for (var m = bqfromDate; m < bqtoDate; m.setMonth(m.getMonth() + 1)) {
            var endMonth = new Date();
            endMonth.setUTCFullYear(m.getUTCFullYear(),m.getUTCMonth() + 1,0);
            endMonth.setUTCHours(0,0,0,0);
            months[i] = endMonth.getTime();
            i++;
        }
    }
    return months;
}



