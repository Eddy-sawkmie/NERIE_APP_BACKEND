function callCustomAjax(url, data, success)
{
    $.ajax({
        type: "POST",
        url: url,
        data: data,
        success: function (data) {
            success(data);
        },
        error: (jqXHR, textStatus, errorThrown) => handleErrorResponse(jqXHR, textStatus, errorThrown)
    });

}

function callCustomAjaxasync(url, data, success)
{
    $.ajax({
        type: "POST",
        async: false,
        url: url,
        data: data,
        success: function (data) {
            success(data);
        },
        error: (jqXHR, textStatus, errorThrown) => handleErrorResponse(jqXHR, textStatus, errorThrown)
    });
}

function handleErrorResponse(jqXHR, textStatus, errorThrown) {
    switch (jqXHR.status) {
        case 400: // Bad Request
            alert(`[400] BAD REQUEST: ${jqXHR.responseText}`)
            break
        case 401: // Unauthorized
            window.location.href = redirect('error/401')
            break
        case 404:   // Not Found
            window.location.href = redirect('error/404')
            break
        case 500: // Server Error
            if (jqXHR.responseText)
                window.location.href = redirect(`error/500?message=${encodeURIComponent(jqXHR.responseText)}`)
            break
        default:
            alert("error:" + textStatus + " - exception:" + errorThrown)
    }
}

function initdatatable(tableid, title) {
    return $('#' + tableid).DataTable({
        dom: 'Blfrtip',
        pageLength: 10,
        lengthMenu: [[10, 25, 50, -1], [10, 25, 50, "All"]],
        buttons: [
            {
                extend: 'excelHtml5',
                text: '<i class="fa fa-file-excel-o"></i> Excel',
                title: title,
                exportOptions: {
                    columns: ':visible:not(.noExport)'
                },
                className: 'btn btn-sm btn-outline-success'
            },
            {
                extend: 'pdfHtml5',
                text: '<i class="fa fa-file-pdf-o"></i> PDF',
                title: title,
                exportOptions: {
                    columns: ':visible:not(.noExport)'
                },
                className: 'btn btn-sm btn-outline-danger'
            }
        ],
        autoWidth: false
    });
}



