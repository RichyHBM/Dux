function padNumberToXDecimals(numberStr, amount) {
    numberStr = '' + numberStr;
    for(var i = numberStr.length; i < amount; i++)
        numberStr = '0' + numberStr;
    return numberStr;
}

function dateToYYYYMMDD_HHMMSS(date) {
    return date.getUTCFullYear() +
        '-' + padNumberToXDecimals(date.getUTCMonth() + 1, 2) +
        '-' + padNumberToXDecimals(date.getUTCDate(), 2) +
        ' ' + padNumberToXDecimals(date.getUTCHours(), 2) +
        ':' + padNumberToXDecimals(date.getUTCMinutes(), 2) +
        ':' + padNumberToXDecimals(date.getUTCSeconds(), 2);
}

var date_YYYYMMDD_HHMMSS_ValidationRegex = /^\d{4}\-(0[0-9]|1[0-2])\-(0[1-9]|[12][0-9]|3[01])\s([01][0-9]|2[0-3]):([0-5][0-9]):([0-5][0-9])$/;
