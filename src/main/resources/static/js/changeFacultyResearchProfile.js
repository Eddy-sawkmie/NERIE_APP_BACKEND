$(document).ready(function () {
                // Faculty Profile Update
                $("#faculty").submit(function (ed) {
                    ed.preventDefault(); 
                    $.ajax({
                        type: "POST",
                        url: API.postFacultyResearchProfile,
                        // headers: {
                        //     "X-CSRF-TOKEN": document.querySelector("input[name='_csrf']").value
                        // },
                        data: {
                            gscholarlink: $("#gscholarlink").val(),
                            orcid: $("#orcid").val(),
                            academicqualification: $("#academicqualification").val(),
                            areaofspecialization: $("#areaofspecialization").val(),
                            areaofinterest: $("#areaofinterest").val(),
                            briefprofile: $("#briefprofile").val(),
                            researchprojects: $("#researchprojects").val()
                        },
                        dataType: "text",
                        success: function(data) {
                            if (data === "1") {
                                alert("Updated Profile Successfully");
                                window.location.reload();
                            } else {
                                alert("Something went wrong");
                            }
                        }
                    });
                });

                // Delete Paper
                $(".deletePaperBtn").click(function() {
                    var paperId = $(this).data("id");
                    if(confirm("Are you sure you want to delete this research paper?")) {
                        $.ajax({
                            url: API.deleteResearchPaper,
                            type: "POST",
                            // headers: {
                            //     "X-CSRF-TOKEN": $("#rpcsrf").val()
                            // },
                            data: { researchpaperid: paperId },
                            success: function(response) {
                                if(response === "1") {
                                    alert("Research paper deleted successfully!");
                                    window.location.reload();
                                } else {
                                    alert("Error deleting research paper!");
                                }
                            },
                            error: function(xhr, status, error) {
                                alert("Error: " + error);
                            }
                        });
                    }
                });

                // Edit Paper populate form
                $(".editPaperBtn").click(function() {
                    var btn = $(this);
                    $("#researchpaperid").val(btn.data("id"));
                    $("#title").val(btn.data("title"));
                    $("#journal").val(btn.data("journal"));
                    $("#year").val(btn.data("year"));
                    $("#authors").val(btn.data("authors"));
                    $("#publisher").val(btn.data("publisher"));
                    $("#category").val(btn.data("category"));
                    $("#link").val(btn.data("link"));

                    $("#submitResearchPaper").text("Update Research Paper").removeClass("btn-primary").addClass("btn-success");
                    $("#ResearchPaperAccText").text("Update Research Paper");
                    
                    $('html, body').animate({
                        scrollTop: $("#researchPaperForm").offset().top - 100
                    }, 600);
                });

                // Submit Add/Update Paper
                $("#submitResearchPaper").click(function() {
                    if (!$("#title").val().trim() || !$("#journal").val().trim() || !$("#year").val().trim() || !$("#authors").val().trim()) {
                        alert("Please fill in all required fields (Title, Journal, Year, Authors).");
                        return;
                    }
                
                    var paperId = $("#researchpaperid").val(); 
                    var url = paperId ? API.updateResearchPaper : API.addResearchPaper;

                    $.ajax({
                        type: "POST",
                        url: url,
                        // headers: {
                        //     "X-CSRF-TOKEN": $("#rpcsrf").val()
                        // },
                        data: {
                            researchpaperid: paperId,
                            title: $("#title").val(),
                            journal: $("#journal").val(),
                            year: $("#year").val(),
                            category: $("#category").val(),
                            publisher: $("#publisher").val(),
                            authors: $("#authors").val(),
                            link: $("#link").val()
                        },
                        success: function(response) {
                            if(response === "1") {
                                alert("Research paper " + (paperId ? "updated" : "added") + " successfully!");
                                window.location.reload();
                            } else {
                                alert("Error saving research paper!");
                            }
                        },
                        error: function(xhr, status, error) {
                            alert("Error: " + error);
                        }
                    });
                });
            });