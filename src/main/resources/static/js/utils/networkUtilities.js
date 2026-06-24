function handleAjaxError(jqXHR, textStatus, errorThrown) {
    switch (jqXHR.status) {
        case 400:
            Notiflix.Notify.Failure(`[400] BAD REQUEST: ${jqXHR.responseText}`)
            break
        case 401:
            Notiflix.Confirm.Ask(
                '[401] UNAUTHORIZED',
                'You are unauthenticated. Log in again?',
                '',
                'Yes',
                'No',
                () => {
                    window.location.href = redirectLoginURL
                }
            )
            break
        case 403:
            Notiflix.Notify.Failure(`[403] FORBIDDEN${jqXHR.responseText ? `: ${jqXHR.responseText}` : ''}`)
            break
        case 404:
            Notiflix.Notify.Failure(`[404] NOT FOUND${jqXHR.responseText ? `: ${jqXHR.responseText}` : ''}`)
            break
        case 500:
            Notiflix.Notify.Failure(`[500] SERVER ERROR: Something went wrong`)
            break
        default:
            Notiflix.Notify.Failure("error:" + textStatus + " - exception:" + errorThrown)
    }
}

function handleFetchError(status, msg) {
    switch (status) {
        case 400:
            Notiflix.Notify.Failure(`[400] BAD REQUEST: ${msg}`)
            break
        case 401:
            Notiflix.Confirm.Ask(
                '[401] UNAUTHORIZED',
                'You are unauthenticated. Log in again?',
                '',
                'Yes',
                'No',
                () => {
                    window.location.href = redirectLoginURL
                }
            )
            break
        case 403:
            Notiflix.Notify.Failure(`[403] FORBIDDEN${msg ? `: ${msg}` : ''}`)
            break
        case 404:
            Notiflix.Notify.Failure(`[404] NOT FOUND${msg ? `: ${msg}` : ''}`)
            break
        case 500:
            Notiflix.Notify.Failure(`[500] SERVER ERROR: Something went wrong`)
            break
        default:
            Notiflix.Notify.Failure("error:" + textStatus + " - exception:" + errorThrown)
    } 
}

