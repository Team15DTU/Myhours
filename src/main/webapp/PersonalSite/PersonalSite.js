//Dette metoderne til tabellen

function selectShift(name) {


    var selector = document.getElementById(name);

    var allShift = findAllShift();

    while(selector.hasChildNodes()){
        selector.removeChild(selector.firstChild);
    }
    selector.appendChild(document.createElement('option'))

    while(allShift.length > 0) {

        var currentShift = allShift.pop();
        var option = document.createElement('option');
        var name = currentShift[2].getDate() + '/' + (currentShift[2].getMonth()+1) + ' at ' + currentShift[2].getHours() + ':' + currentShift[2].getMinutes() + ' to ' + currentShift[3].getHours() + ':' + currentShift[3].getMinutes();
        option.appendChild(document.createTextNode(name));
        option.value = currentShift[0];

        selector.appendChild(option);
    }
}


function currentShiftToEdit() {

    var mylist = document.getElementById('select');
    var shift = findShift(mylist.options[mylist.selectedIndex].value);



    document.getElementById('shiftEditInfo').innerHTML = ' i ' + findWorkplace(shift[1]);
/*
    var shiftEditStart = shift[2].getFullYear() + '-' + (shift[2].getMonth()-1) + '-' + shift[2].getDate() + 'T' + shift[2].getHours + ':' + shift[2].getMinutes;

    Begge disse 2 variabler skal ændre value til en html "dateTime-local" værdi.

    document.getElementById('shift_edit_start').value = shiftEditStart;
    document.getElementById('shift_edit_end').value = shiftEditStart;
*/
    document.getElementById('shift_edit_break').value = shift[4];

}

function deleteShiftFromHTTP() {

    var mylist = document.getElementById('select3');
    deleteShift(mylist.options[mylist.selectedIndex].value);



}



function selectJobs(name) {
    var selector = document.getElementById(name);
    var allJobs = findAllJobs();

    while(selector.hasChildNodes()){
        selector.removeChild(selector.firstChild);
    }
    selector.appendChild(document.createElement('option'))

    for (var i = 0; i < findAllJobs().length; i++) {
        var currentJob = allJobs.pop();
        var option = document.createElement('option');
        var name = currentJob[5] + ' at ' + currentJob[2];
        option.appendChild(document.createTextNode(name));
        option.value = currentJob[0];
        selector.appendChild(option);

    }
}






function shifts() {

    var table = document.getElementById('left_table');
    while(table.hasChildNodes()){
        table.removeChild(table.firstChild);
    }

    var vagt = 'Vagt';
    var job = 'Job';
    var salary = 'Løn';

    var row = table.insertRow(0);
    row.insertCell(0).innerHTML = vagt.bold();
    row.insertCell(1).innerHTML = job.bold();
    row.insertCell(2).innerHTML = salary.bold();
    var i;

    for (i = 0; i < 3; i++){
        var row2 = table.insertRow(i+1);
        /*
        noget er galt, når jeg køre findshift() køre den kun igennem en gang, skal lige se hvordan det kan fixes
        var shift = findShift(i);
        */
        row2.insertCell(0).innerHTML = 'Midlertidig fylder ';
        row2.insertCell(1).innerHTML = findWorkplace(i);
        row2.insertCell(2).innerHTML = findPaycheck(i);
    }

}

function job() {

    var table = document.getElementById('left_table');
    while(table.hasChildNodes()){
        table.removeChild(table.firstChild);
    }
    var firm = 'Firma';
    var lastpay = 'Sidste udbetaling';
    var recivepay = 'Optjent løn';


    var row = table.insertRow(0);
    row.insertCell(0).innerHTML = firm.bold();
    row.insertCell(1).innerHTML = lastpay.bold();
    row.insertCell(2).innerHTML = recivepay.bold();
    var i;

    for (i = 0; i < 1; i++){
        var row2 = table.insertRow(i+1);
        row2.insertCell(0).innerHTML = findCompany(i);
        row2.insertCell(1).innerHTML = lastPaycheck(i)+' Kr.';
        row2.insertCell(2).innerHTML = estimatePaycheck(i)+' Kr.';
    }
}

function workplace() {
    var table = document.getElementById('left_table');
    while(table.hasChildNodes()){
        table.removeChild(table.firstChild);
    }

    var firm = 'Firma';

    var row = table.insertRow(0);
    row.insertCell(0).innerHTML = firm.bold();
    var i;

    for (i = 0; i < 3; i++){
        var row2 = table.insertRow(i+1);
        row2.insertCell(0).innerHTML = findCompany(i);
    }
}

function none() {
    var table = document.getElementById('left_table');
    while(table.hasChildNodes()){
        table.removeChild(table.firstChild);
    }
}


//her er de metoder til  felterne som skal snakke med serveren

function findCompany(nr) {
    return 'irma'
}

function findNrOfCompanys() {
    return 3;
}

function findCompanyId(nr) {
    if (nr == 0){return 1;}
    if (nr == 1){return 2;}
    else {return 3}
}

function lastPaycheck(nr) {
    return 2500;
}

function estimatePaycheck(nr) {
    return 1750;
}

function findShift(id) {


    if (id == 0) {
       var shift = [0,1,new Date(), new Date(), 5, 3]
        return shift;
    } else if (id == 1){
        var shift = [1,2,new Date(), new Date(), 10, 3]
        return shift;
    } else if (id == 2) {
        var shift = [2,3,new Date(), new Date(), 20, 3]
        return shift;
    } else {
        var shift = [3,0,new Date(), new Date(), 25, 3]
        return shift;
    }


    return 'shift';
}

function findAllShift(nr) {
    var allShift = [];

    for (var i = 0; i < 5; i++) {
        var endDate = new Date();
        var startDate = new Date();

        var shift = [i, 0, startDate, endDate, 30, findWorkplace(i)]
        allShift.push(shift);
    }


    return allShift;

}

function deleteShift(shiftID) {

}


function findWorkplace(nr) {
    if (nr == 0) {return 'kvikly'}
    else if (nr == 1) {return 'fakta'}
    else if (nr == 2) {return 'irma'}
    else if (nr == 3) {return 'Vektor'}
}

function findAllJobs(userID) {

    var jobs = [];
    var kvikly = [0, 66, 'Kvikly', 110, new Date(), 'Flaske dreng'];
    var fakta = [1, 66, 'Fakta', 115, new Date(), 'Kasse assistent'];
    var irma = [2, 66, 'Irma', 110, new Date(), 'service medarbejder'];
    var studieStarten = [3, 66, 'Studie starten', 115, new Date(), 'Vektor'];

    jobs.push(kvikly);
    jobs.push(fakta);
    jobs.push(irma);
    jobs.push(studieStarten);

    return jobs;
}



function findPaycheck(nr) {
    return '444 kr.'
}


//her er graferne

function daysInMonth (month, year) {
    return new Date(year, month, 0).getDate();
}

function hoursOfWork () {

    return Math.random()*10;
}




// Show only one item at a time in the menu
var checkMenu0 = function() {
    //$("#myid").attr("checked");
    $("#myid1").prop("checked", false);
    $("#myid2").prop("checked", false);
    $("#myid3").prop("checked", false);
};

var checkMenu1 = function() {
    //$("#myid").attr("checked");
    $("#myid").prop("checked", false);
    $("#myid2").prop("checked", false);
    $("#myid3").prop("checked", false);
};
var checkMenu2 = function() {
    //$("#myid").attr("checked");
    $("#myid1").prop("checked", false);
    $("#myid").prop("checked", false);
    $("#myid3").prop("checked", false);
};

var checkMenu3 = function() {
    //$("#myid").attr("checked");
    $("#myid1").prop("checked", false);
    $("#myid2").prop("checked", false);
    $("#myid").prop("checked", false);
};
