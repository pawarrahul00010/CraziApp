function resetLocMsg(){
	document.getElementById("locIdErr").innerHTML = "";
	document.getElementById("locNameErr").innerHTML = "";
	document.getElementById("locTypeErr").innerHTML = "";
}
function validateLoc() {
		var exp1 = /^[1-9][0-9]*$/;
		var exp2 = /^[A-Z]{3,8}$/;
	    var flag=true;
	    resetLocMsg();
		if (!(document.locForm.locId.value.match(exp1))) {
			document.getElementById("locIdErr").innerHTML = " * Invalid id entered *";
			flag = false;
		}
		if (!(document.locForm.locName.value.match(exp2))) {
			document.getElementById("locNameErr").innerHTML = " * Invalid Name entered *";
			flag = false;
		}
		if(document.locForm.locType[0].checked==false && document.locForm.locType[1].checked==false){
			document.getElementById("locTypeErr").innerHTML = " * Please choose any one *";
			flag = false;
		}

		return flag;
}