function countCharacters(event, elementId, maxLen)
{
    var curLen = jQuery("#" + elementId).val().length;
    if (Number(curLen) === Number(maxLen) + 1)
    {
        event.preventDefault();
    }
}

function showMsg(id, Msg)
{
    var idMsg = id + "Msg";
    if (Msg !== '')
    {
        jQuery('#' + idMsg + '').html("&nbsp;&nbsp;<font color='#B92500'>" + Msg + "...!</font>&nbsp;&nbsp;");
        jQuery('#' + id + '').focus();
    } else {
        jQuery('#' + idMsg + '').html("");
    }
}

//----------Password Strength Starts-----------//
function PasswordStrength(userid, password)
{
    var bpos = "";
    var perc = 0;
    jQuery('#' + password).keyup(function () {
        jQuery('#results').html(passwordStrength(jQuery('#' + password).val(), jQuery('#' + userid).val()));
        perc = passwordStrengthPercent(jQuery('#' + password).val(), jQuery('#' + userid).val());

        bpos = " jQuery('#colorbar').css( {backgroundPosition: \"0px -";
        bpos = bpos + perc + "px";
        bpos = bpos + "\" } );";
        bpos = bpos + " jQuery('#colorbar').css( {width: \"";
        bpos = bpos + (perc * 2) + "px";
        bpos = bpos + "\" } );";
        eval(bpos);
        jQuery('#percent').html(" " + perc + "% ");
    })

    jQuery('#' + userid).keyup(function () {
        jQuery('#results').html(passwordStrength(jQuery('#' + password).val(), jQuery('#' + userid).val()));
        perc = passwordStrengthPercent(jQuery('#' + password).val(), jQuery('#' + userid).val());

        bpos = " jQuery('#colorbar').css( {backgroundPosition: \"0px -";
        bpos = bpos + perc + "px";
        bpos = bpos + "\" } );";
        bpos = bpos + " jQuery('#colorbar').css( {width: \"";
        bpos = bpos + (perc * 2) + "px";
        bpos = bpos + "\" } );";
        eval(bpos);
        jQuery('#percent').html(" " + perc + "% ");
    })

}
//----------Password Strength Ends-----------//

function loadDatePicker(id) {
    $('#' + id).datetimepicker({
        viewMode: 'years',
        format: 'YYYY-MM-DD'
    });
}
function loadTimePicker(id) {
    $('#' + id).datetimepicker({
        format: 'LT'
    });
}
//==============================================//

function initAuditrail(actiontaken)
{
    var userid = jQuery("#session_userid").val();
    jQuery.ajax({
        async: false,
        cache: false,
        url: "initAuditrail.htm",
        data: {'userid': userid, 'actiontaken': actiontaken},
        type: "POST",
        success: function ()
        {

        },
        error: function (e) {
            //alert(e.status);
        }
    });
}
//For formatting date
//dd/mm/yyyy to yyyy-mm-dd
function rawdateToStandard(d) {
    return d.split("-")[2] + "/" + d.split("-")[1] + "/" + d.split("-")[0];
}

//for formatting date
//yyyy-mm-dd to dd/mm/yyyy
function standardToRawdate(d) {
    return d.split("/")[2] + "-" + d.split("/")[1] + "-" + d.split("/")[0];
}
