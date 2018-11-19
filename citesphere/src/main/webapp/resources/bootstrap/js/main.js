$(function() {
    $(".date").each(function(index, element) {
        var dateStr = $(element).text();
        var parsedDate = new Date(dateStr);
        $(element).text(parsedDate.toLocaleString());
    });    
});