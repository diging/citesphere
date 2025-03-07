

var affCount = 0;
var affiliationTemplate = $($.parseHTML('<div class="aff-entry">' +
		  '<hr>' + 
		  '<div class="form-group">' +
		    '<div class="pull-right remove-aff"><a>Remove</a></div>' +
		    '<label for="affiliation">Affiliation:</label>' + 
		    '<input type="text" class="form-control" id="firstName" placeholder="Affiliation">' +
		  '</div>' +
		  '<button class="btn btn-primary search" data-toggle="modal" data-target="#selectAuthorityModel"' +
			'style="margin-left: 76%" disabled>Search Affiliation <i id="searchSpinner"class="fas fa-spinner fa-spin text-info" style="color: white" hidden></i>' +
		  '</button>' +
		  '<div class="form-group">' +
			'<label for="uri">Affiliation URI:</label>' +
		    	'<input type="text" class="form-control affUri" placeholder="URI"/>' +
		  '</div>' +
		'</div>')
);

// $(document).ready(function() {
// 	console.log("Doc ready");
// 			if( citation == null){
// 			$("#items").val("${defaultItemType}");
// 		}
// 		loadFields();
// 		$('#items').on("change", function(e){
// 			loadFields();
// 		});  
// 	$("#uriLoadingSpinnerAuthor").hide();
// 	$("#uriLoadingFailureAuthor").hide();
// 	$("#uriLoadingFoundAuthor").hide();
// 	$("#searchAuthorSpinner").hide();
	
// 	$("#uriLoadingFoundAuthor").popover();
// 	$("#uriLoadingFailureAuthor").popover();
	
// 	$("#uriLoadingSpinnerEditor").hide();
// 	$("#uriLoadingFailureEditor").hide();
// 	$("#uriLoadingFoundEditor").hide();
// 	$("#searchEditorSpinner").hide();
	
// 	$("#uriLoadingFoundEditor").popover();
// 	$("#uriLoadingFailureEditor").popover();
	
// 	$("#uriLoadingSpinnerCreator").hide();
// 	$("#uriLoadingFailureCreator").hide();
// 	$("#uriLoadingFoundCreator").hide();
// 	$("#searchCreatorSpinner").hide();
	
// 	$("#uriLoadingFoundCreator").popover();
// 	$("#uriLoadingFailureCreator").popover();
	
// 	$("#submitForm").click(function(e) {
// 		constructPersonArray("author", "author", 0);
// 		constructPersonArray("editor", "editor", 0);
// 		var creatorSubmitCount = 0;
// 		$(".creator-row").each(function(idx, elem){
// 			var ele = $(elem).children().first();
// 			if(!(ele.attr("id")=="editor" || ele.attr("id")=="author")) {
// 				var roleCount = constructPersonArray("creator", ele.attr("id"), creatorSubmitCount);
// 				creatorSubmitCount = creatorSubmitCount + roleCount;
// 			}
// 		});
		
// 		createConceptTags();
// 	});
	
// 	/* Handle Author events */
// 	$("#addAuthorButton").click(function() {
// 		savePersonDetails("Author", "Author");
// 	});
	
// 	$("#addAuthorModalCancel").click(function() {
// 		$("#authorModal").modal('hide');
// 		resetPersonCreationModal("Author");
// 	});
	
// 	/* Disable search authority button when first name and last name fields are empty*/
//     $("#firstNameAuthor").keyup(function(e){
//     	allowSearchAndAdd("Author");
//     });
    
//     $("#lastNameAuthor").keyup(function(e){
//     	allowSearchAndAdd("Author");
//     });
    
//     $("#firstNameEditor").keyup(function(e){
//     	allowSearchAndAdd("Editor");
//     });
		
//     $("#lastNameEditor").keyup(function(e){
//     	allowSearchAndAdd("Editor");
//     });
    
//     $("#firstNameCreator").keyup(function(e){
//     	allowSearchAndAdd("Creator");
//     });
		
//     $("#lastNameCreator").keyup(function(e){
//     	allowSearchAndAdd("Creator");
//     });
	
//     $(document).on("click", ".remove-aff", function() {
//     	$(this).closest('.aff-entry').remove();
//     });
    
//     $("#searchAuthor").click(function() {
//         searchAuthorities('Author', 'Author');
//     });

//     $("#searchEditor").click(function() {
//         searchAuthorities('Editor', 'Editor');
//     });

//     $("#searchCreator").click(function() {
//         searchAuthorities('Creator', 'Creator');
//     }); 
	
// 	$(".edit-author").click(function(){
// 		var authorItem = $(this).parent();
// 		editPerson('Author', authorItem[0]);
// 	});
	
// 	$(".edit-author").css('cursor', 'pointer');
	
// 	$(".remove-author").click(removePerson);
// 	$(".remove-author").css('cursor', 'pointer');
// 	$(".remove-concept").click(removeConcept);
// 	$(".remove-concept").css('cursor', 'pointer');
	
// 	$("#addAuthorAffiliation").click(function() {
// 		addAffiliation("Author", affCount + 1);
// 	});
	
// 	$("#addEditorAffiliation").click(function() {
// 		addAffiliation("Editor", affCount + 1);
// 	});
	
// 	$("#addCreatorAffiliation").click(function() {
// 		addAffiliation("Creator", affCount + 1);
// 	});
	
// 	$("#authorIconContainer").on('click', ".popover #authorCreateAuthority", function() {
// 		var uri = $("#uriLoadingFoundAuthor").attr('data-authority-uri');
// 		importAuthorityURL = "/auth/authority/import?uri="+uri;
// 		$.ajax({
// 			dataType: "json",
// 			type: 'POST',
// 			url: importAuthorityURL,
// 			data: {csrfParameterName: csrfToken},
// 			async: false,
// 			success: function(data) {
// 				$("#authorCreateAuthority").hide();
// 				$("#uriAuthorLocalId").val(data['id']);
// 				$("#authorAuthorityUsed").html("Created new authority entry <i>" + data['name'] + "</i>.");
// 				$("#authorAuthorityCreationFeedback").html('<div class="text-success" style="margin-top:10px;">Authority entry has been created!</div>');
// 				showPersonNameInModal(data['name'], "Author");
// 				$("#uriLoadingFoundAuthor").popover('hide');
// 				$("#addAuthorButton").prop("disabled", false);
// 			}
// 		});
// 	});
	
// 	$("#authorIconContainer").on('click', ".popover .foundAuthorities li a", function(event) {
// 		var authId = $(this).attr('data-authority-id');
// 		$("#uriAuthorLocalId").val(authId);
// 		$("#authorAuthorityUsed").html("Using stored authority entry <i>" + $(this).attr('data-authority-name') + "</i>.");
// 		showPersonNameInModal($(this).attr('data-authority-name'), "Author");
// 		$("#uriLoadingFoundAuthor").popover('hide');
// 		$("#addAuthorButton").prop("disabled", false);
// 		event.preventDefault();
// 	});	

// 	var timer = null;
// 	$("#uriAuthor").change(function() {
// 		resetPersonAuthorityCreation("Author");
// 		$("#uriLoadingSpinnerAuthor").show();
// 		var uri = $("#uriAuthor").val();
// 		clearTimeout(timer); 
// 	    timer = setTimeout(function() {
// 	    	getPersonAuthority(uri, "Author");
// 	    }, 1000);
// 	});

// 	$("#closeAuthoritySearchResult").click(function() {
// 		$("#selectAuthorityModel").modal('hide');
// 		$('#selectAuthorityModel a:first').tab('show');
// 	});
	
// 	/* Handle editor events */
// 	$(".edit-editor").click(function(){
// 		var editorItem = $(this).parent();
// 		editPerson('Editor', editorItem[0]);
// 	});
	
// 	$(".edit-editor").css('cursor', 'pointer');
	
// 	$("#addEditorButton").click(function() {
// 		savePersonDetails('Editor', 'Editor');
// 	});

// 	$("#addEditorModalCancel").click(function() {
// 		$("#editorModal").modal('hide');
// 		resetPersonCreationModal("Editor");
// 	});
	
// 	$(".remove-editor").click(removePerson);
// 	$(".remove-editor").css('cursor', 'pointer');
	
// 	$("#uriEditor").change(function() {
// 		resetPersonAuthorityCreation("Editor");
// 		$("#uriLoadingSpinnerEditor").show();
// 		var uri = $("#uriEditor").val();
// 		clearTimeout(timer); 
// 	    timer = setTimeout(function() {
// 	    	getPersonAuthority(uri, "Editor");
// 	    }, 1000);
// 	});
	
// 	$("#editorIconContainer").on('click', ".popover #editorCreateAuthority", function() {
// 		var uri = $("#uriLoadingFoundEditor").attr('data-authority-uri');
// 		importAuthorityURL = "/auth/authority/import?uri="+uri;
// 		$.ajax({
// 			dataType: "json",
// 			type: 'POST',
// 			url: importAuthorityURL,
// 			data: {csrfParameterName:csrfToken},
// 			async: false,
// 			success: function(data) {
// 				$("#editorCreateAuthority").hide();
// 				$("#uriEditorLocalId").val(data['id']);
// 				$("#editorAuthorityUsed").html("Created new authority entry <i>" + data['name'] + "</i>.");
// 				$("#editorAuthorityCreationFeedback").html('<div class="text-success" style="margin-top:10px;">Authority entry has been created!</div>');
// 				showPersonNameInModal(data['name'], "Editor");
// 				$("#uriLoadingFoundEditor").popover('hide');
// 				$("#addEditorButton").prop("disabled", false);
// 			}
// 		});
// 	});
	
// 	$("#editorIconContainer").on('click', ".popover .foundAuthorities li a", function(event) {
// 		var authId = $(this).attr('data-authority-id');
// 		$("#uriEditorLocalId").val(authId);
// 		$("#editorAuthorityUsed").html("Using stored authority entry <i>" + $(this).attr('data-authority-name') + "</i>.");
// 		showPersonNameInModal($(this).attr('data-authority-name'), "Editor");
// 		$("#uriLoadingFoundEditor").popover('hide');
// 		$("#addEditorButton").prop("disabled", false);
// 		event.preventDefault();
// 	});
	
// 	/* Handle Other Creators events */
// 	$("#addCreatorButton").click(function(e) {
// 		var target = $(e.target);
// 		if(target.attr("data-creator-type") != null) {			
// 			savePersonDetails(target.attr("data-creator-type"), "Creator");
// 		} else {
// 			savePersonDetails("", "Creator");
// 		}
// 	});
	
// 	$(".creatorModalLink").click(function(e) {
// 		var target = $(e.target);
// 		var creatorType = target.attr("data-creator-type").charAt(0).toUpperCase() + target.attr("data-creator-type").slice(1);
// 		$("#creatorLabel").text("Enter "+creatorType+" Information");
// 		$("#addCreatorButton").text("Add "+creatorType);
// 		$("#addCreatorButton").attr("data-creator-type", target.attr("data-creator-type"));
// 	});
	
// 	$("#addCreatorModalCancel").click(function() {
// 		$("#creatorModal").modal('hide');
// 		resetPersonCreationModal("Creator");
// 	});
	
// 	$(".edit-creator").click(function(){
// 		var creatorItem = $(this).parent();
// 		editPerson('Creator', creatorItem[0]);
// 	});
	
// 	$(".edit-creator").css('cursor', 'pointer');
		
// 	$(".remove-creator").click(removePerson);
// 	$(".remove-creator").css('cursor', 'pointer');
	
// 	$("#uriCreator").change(function() {
// 		resetPersonAuthorityCreation("Creator");
// 		$("#uriLoadingSpinnerCreator").show();
// 		var uri = $("#uriCreator").val();
// 		clearTimeout(timer); 
// 	    timer = setTimeout(function() {
// 	    	getPersonAuthority(uri, "Creator");
// 	    }, 1000);
// 	});
	
// 	$("#creatorIconContainer").on('click', ".popover #creatorCreateAuthority", function() {
// 		var uri = $("#uriLoadingFoundCreator").attr('data-authority-uri');
// 		importAuthorityURL = "/auth/authority/import?uri="+uri;
// 		$.ajax({
// 			dataType: "json",
// 			type: 'POST',
// 			url: importAuthorityURL,
// 			data: {csrfParameterName:csrfToken},
// 			async: false,
// 			success: function(data) {
// 				$("#creatorCreateAuthority").hide();
// 				$("#uriCreatorLocalId").val(data['id']);
// 				$("#creatorAuthorityUsed").html("Created new authority entry <i>" + data['name'] + "</i>.");
// 				$("#creatorAuthorityCreationFeedback").html('<div class="text-success" style="margin-top:10px;">Authority entry has been created!</div>');
// 				showPersonNameInModal(data['name'], "Creator");
// 				$("#uriLoadingFoundCreator").popover('hide');
// 				$("#addCreatorButton").prop("disabled", false);
// 			}
// 		});
// 	});
	
// 	$("#creatorIconContainer").on('click', ".popover .foundAuthorities li a", function(event) {
// 		var authId = $(this).attr('data-authority-id');
// 		$("#uriCreatorLocalId").val(authId);
// 		$("#creatorAuthorityUsed").html("Using stored authority entry <i>" + $(this).attr('data-authority-name') + "</i>.");
// 		showPersonNameInModal($(this).attr('data-authority-name'), "Creator");
// 		$("#uriLoadingFoundCreator").popover('hide');
// 		$("#addCreatorButton").prop("disabled", false);
// 		event.preventDefault();
// 	});
	
// 	/* adding concepts */
// 	$("#addConceptButton").click(function(e) {
// 		e.preventDefault();
		
// 		var conceptId = $("#addConceptConceptSelect");
// 		var conceptType = $("#addConceptTypeSelect");
		
// 		var conceptSpan = $('<span class="badge"></span>');
// 		conceptSpan.attr("data-concept-uri", conceptId.val());
// 		conceptSpan.attr("data-type-uri", conceptType.val());
		
// 		var text = $("#addConceptConceptSelect option:selected").text();
// 		var typeName = $("#addConceptTypeSelect option:selected").text();
// 		conceptSpan.text(text + " | " + typeName + " ");
// 		var deleteIcon = $('<i class="icon-circle-close remove-concept" style="cursor: pointer; color: white; font-size: 12px;"></i>');
// 		deleteIcon.click(removeConcept);
// 		conceptSpan.append(deleteIcon);
// 		$("#conceptTags").append(conceptSpan);
		
// 		$("#addConceptModal").modal('hide');
// 	});
// });

/*Clones the affiliation template, modifies it appropriately and appends it to the given modal type*/
function addAffiliation(modalType, counter) {
	modalTypeL = modalType.toLowerCase();
	var affiliationCopy = affiliationTemplate.clone();
	affiliationCopy.attr('id', modalTypeL+'AffiliationTemplate'+counter);
	affiliationCopy.addClass('aff-info');
	
	affiliationCopy.find('label[for="affiliation"]').attr('for', 'firstName'+modalType+'Affiliation'+counter);
	affiliationCopy.find('label[for="uri"]').attr('for', 'uri'+modalType+'Affiliation'+counter);
	
	var affInput = affiliationCopy.find('#firstName');
	affInput.addClass('firstName'+modalType+'Affiliation');
	affInput.attr('id', 'firstName'+modalType+'Affiliation'+counter);
	affInput.attr('data-client-id', affInput.attr('id'));
	
	var searchButton = affiliationCopy.find('button');
	searchButton.attr('id', 'search'+modalType+'Affiliation'+counter);
	searchButton.attr('data-client-id', searchButton.attr('id'));
	searchButton.find('#searchSpinner').attr('id', 'search'+modalType+'Affiliation'+counter+'Spinner');
	searchButton.prop('disabled', true);
	
	var affUri = affiliationCopy.find('.affUri');
	affUri.attr('id', 'uri'+modalType+'Affiliation'+counter);
	affUri.attr('data-client-id', affUri.attr('id'));
	
	$("#"+modalTypeL+"Affiliations").append(affiliationCopy);
	
	var affiliationCounter = modalType + 'Affiliation' + counter;
	$('#firstName'+affiliationCounter).keyup(function(e) {
		allowSearchAndAdd(affiliationCounter);
	});
	
	$("#search"+affiliationCounter).click(function() {
		searchAuthorities(affiliationCounter, affiliationCounter);
	});
	
	affCount++;
}

function searchAuthorities(modalType, personType) {
	$("#selectAuthorityModel").on('hidden.bs.modal', function(e) {
	    $('#selectAuthorityModel a:first').tab('show');
	})  
	$("#search"+modalType+"Spinner").show();
	$('#userAuthority-pagination-top').twbsPagination('destroy');
	$('#groupAuthority-pagination-top').twbsPagination('destroy');
	$('#conceptpowerAuthority-pagination-top').twbsPagination('destroy');
	$('#viafAuthority-pagination-top').twbsPagination('destroy');
	
	getUserAuthorities(modalType, personType, 0)
	getViafAuthorities(modalType, personType, 0)
	getGroupAuthorities(modalType, personType, 0)
	getconceptpowerAuthorities(modalType, personType, 0)
	console.log("done")
	$("#search"+modalType+"Spinner").hide();
}

function allowSearchAndAdd(element){
    if($("#firstName"+element).val() == "" && $("#lastName"+element).val()==""){
    	$("#search"+element).prop("disabled", true);
    	$("#add"+element+"Button").prop("disabled", true);
    }
    else{
    	$("#search"+element).prop("disabled", false);
    	$("#add"+element+"Button").prop("disabled", false);
    }
}

function showAffiliationNameInModal(name, uri, personType){
	$("#firstName"+personType).val(name);
	$("#uri"+personType).val(uri);
}

/* Function to populate name in modal fetched from uri */
function showPersonNameInModal(name, personType){
	var personName = name;
	
	/* Name containing brackets
	example: Dempsey, Hugh A. (Hugh Aylmer), 1929- */
	if(name.includes("(")) {
		personName = name.substring(0, name.indexOf("("));
	}
	
	/* Name containing title/year
	example: Iqbāl, Muḥammad, Sir, 1877-1938 */
	if(personName.split(",").length > 2) {
		personName = personName.substring(0, personName.indexOf(',', personName.indexOf(",")+1));
	}
	
	/* Name containing span
	example: Dempsey, Patrick, 1966- */
	if(personName.includes("-")) {
		personName = personName.trim();
		personName = personName.substring(0, personName.lastIndexOf(' '));
	}
	
	/* Name separated by comma
	example: Dempsey, Paul Stephen */
	if(personName.indexOf(",") != -1) {
		$("#firstName"+personType).val(personName.substring(personName.indexOf(',')+1).trim());
		$("#lastName"+personType).val(personName.substring(0, personName.lastIndexOf(', ')));
	} else {
		$("#lastName"+personType).val(personName.substring(personName.lastIndexOf(' ')+1).trim());
		$("#firstName"+personType).val(personName.substring(0, personName.lastIndexOf(' ')));
	}
}

/* Function to populate modal on edit */
function editPerson(modalName, item){
	var personItem = $(item);
	var modalNameLCase = modalName.toLowerCase();
	$("#firstName"+modalName).val(personItem.attr("data-"+modalNameLCase+"-firstname"));
	$("#lastName"+modalName).val(personItem.attr("data-"+modalNameLCase+"-lastname"));
	$("#uri"+modalName).val(personItem.attr("data-"+modalNameLCase+"-uri"));
	$("#id"+modalName).attr("data-"+modalNameLCase+"-id", personItem.attr("id"));
	
	var counter = 0;
	personItem.children(".affiliation-class").each(function(idx, elem) {
		var affiliationCopy = affiliationTemplate.clone();
		affiliationCopy.attr('id', modalNameLCase+'AffiliationTemplate'+counter);
		affiliationCopy.addClass('aff-info');
		
		affiliationCopy.find('label[for="affiliation"]').attr('for', 'firstName'+modalName+'Affiliation'+counter);
		affiliationCopy.find('label[for="uri"]').attr('for', 'uri'+modalName+'Affiliation'+counter);
		
		var affInput = affiliationCopy.find('#firstName');
		affInput.addClass('firstName'+modalName+'Affiliation');
		affInput.attr('id', 'firstName'+modalName+'Affiliation'+counter);
		affInput.attr('data-client-id', affInput.attr('id'));
		affInput.attr("data-affiliation-name", $(elem).data("affiliationName"));
		affInput.attr("data-affiliation-id", $(elem).data("affiliationId"));
		affInput.val($(elem).data("affiliationName"));
		
		var searchButton = affiliationCopy.find('button');
		searchButton.attr('id', 'search'+modalName+'Affiliation'+counter);
		searchButton.attr('data-client-id', searchButton.attr('id'));
		searchButton.find('#searchSpinner').attr('id', 'search'+modalName+'Affiliation'+counter+'Spinner');
		searchButton.prop('disabled', false);
		
		var affUri = affiliationCopy.find('.affUri');
		affUri.attr('id', 'uri'+modalName+'Affiliation'+counter);
		affUri.attr('data-client-id', affUri.attr('id'));
		affUri.attr("data-affiliation-uri", $(elem).data("affiliationUri"));
		affUri.val($(elem).data("affiliationUri"));
		
		$("#"+modalNameLCase+"Affiliations").append(affiliationCopy);
		
		var affiliationCounter = modalName + 'Affiliation' + counter;
		$('#firstName'+affiliationCounter).keyup(function(e) {
			allowSearchAndAdd(affiliationCounter);
		});
		
		$("#search"+affiliationCounter).click(function() {
			searchAuthorities(affiliationCounter, affiliationCounter);
		});
		
		counter++;
	});
	/*Need to fast forward the affiliation counter which is used while adding a new affiliation entry*/
	affCount = counter;
	$("#addCreatorButton").attr("data-"+modalNameLCase+"-type", personItem.attr("data-"+modalNameLCase+"type"));
	$("#add"+modalName+"Button").text("Update "+modalName);
	$("#add"+modalName+"Button").prop("disabled", false);
	
	$("#"+modalNameLCase+"Modal").modal('show');
	if($("#firstName"+modalName).val() != "" || $("#lastName"+modalName).val() != ""){
		$("#search"+modalName).prop("disabled", false);
	}
}

/* Function to save information on closing modal */
function savePersonDetails(personType, modalName){
	var modalNameLCase = modalName.toLowerCase();
	var personSpan;
	var personTypeLCase = personType.toLowerCase();
	if($("#id"+modalName).attr("data-"+modalNameLCase+"-id") != null && $("#id"+modalName).attr("data-"+modalNameLCase+"-id").length > 0) {
		personSpan = $('#'+$("#id"+modalName).attr("data-"+modalNameLCase+"-id"));
		personTypeLCase = personSpan.attr("data-creator-type").toLowerCase();
	} else {
		var id = personTypeLCase + $("."+personTypeLCase+"-item").length;
		personSpan = $('<span id='+id+'>');
	}
	
	personSpan.attr("class", "label label-warning "+personTypeLCase +"-item");
	personSpan.html("");
	
	var firstname = $("#firstName"+modalName).val();
	var lastname = $("#lastName"+modalName).val();
	
	var uri = $("#uri"+modalName).val();
	var localAuthority = $("#uri"+modalName+"LocalId").val();
	personSpan.attr("data-"+modalNameLCase+"-firstname", firstname);
	personSpan.attr("data-"+modalNameLCase+"-lastname", lastname);
	personSpan.attr("data-"+modalNameLCase+"-uri", uri);
	personSpan.attr("data-"+modalNameLCase+"-authority-id", localAuthority);
	personSpan.attr("data-creator-type", personTypeLCase);
	
	var affiliationsList = [];
	var affSpan = $("<span>");
	$("#"+modalNameLCase+"Affiliations").children().each(function(idx, elem) {
		var affName = $(elem).find(".firstName"+modalName+"Affiliation");
		var affUri = $(elem).find(".affUri");
		if(affName.val().length!=0){
			var affSpan = $("<span>");
			affSpan.addClass("affiliation-class");
			affSpan.attr("data-affiliation-name", affName.val());
			affSpan.attr("data-affiliation-uri", affUri.val());
			affiliationsList.push(affName.val());
			personSpan.append(affSpan);
		}
	});
	
	var affiliationString = "";
	if (affiliationsList.length != 0) {
		affiliationString = " (" + $.grep(affiliationsList, Boolean).join(", ") + ")";
	}
	
	personSpan.append(lastname + ', ' + firstname + affiliationString + '&nbsp;&nbsp; ');
	var editIcon = $('<i class="icon-edit edit-'+modalNameLCase+'" style="color: white; font-size: 12px;"></i>')
	var deleteIcon = $('<i class="icon-circle-close remove-'+modalNameLCase+'" style="color: white; font-size: 12px;"></i>');
	editIcon.click(function(){
		var personItem = $(this).parent();
		editPerson(modalName, personItem[0]);
	});
	deleteIcon.click(removePerson);
	personSpan.append(editIcon);
	personSpan.append(deleteIcon);
	$("#"+personTypeLCase+"List").append(personSpan);
	$("#"+personTypeLCase+"List").append("&nbsp;&nbsp; ");
	$("#"+modalNameLCase+"Modal").modal('hide');
	resetPersonCreationModal(modalName);
}


/* Function to append final information for form submission */
function constructPersonArray(arrayName, role, iter){
	var creator, otherCreatorCount = 0;
	var roleLC = role.toLowerCase();
	if(arrayName == "creator"){
		creator = "otherCreator";
	} else {
		creator = arrayName;
	}
	$('.'+roleLC+'-item').each(function(idx, person) {
		var creatorSubmitCount = idx + iter;
		var personIdField = $("<input>");
		personIdField.attr("type", "hidden");
		personIdField.attr("id", creator+"s" + creatorSubmitCount + ".id");
		personIdField.attr("name", creator+"s[" + creatorSubmitCount + "].id");
		personIdField.attr("value", $(person).attr("data-"+arrayName+"-id"));
		$("#editForm").append(personIdField);
		
		var personFirstNameField = $("<input>");
		personFirstNameField.attr("type", "hidden");
		personFirstNameField.attr("id", creator+"s" + creatorSubmitCount + ".firstName");
		personFirstNameField.attr("name", creator+"s[" + creatorSubmitCount + "].firstName");
		personFirstNameField.attr("value", $(person).attr("data-"+arrayName+"-firstname"));
		$("#editForm").append(personFirstNameField);
		
		var personLastNameField = $("<input>");
		personLastNameField.attr("type", "hidden");
		personLastNameField.attr("id", creator+"s" + creatorSubmitCount + ".lastName");
		personLastNameField.attr("name", creator+"s[" + creatorSubmitCount + "].lastName");
		personLastNameField.attr("value", $(person).attr("data-"+arrayName+"-lastname"));
		$("#editForm").append(personLastNameField);
		
		var personRoleField = $("<input>");
		personRoleField.attr("type", "hidden");
		personRoleField.attr("id", creator+"s" + creatorSubmitCount + ".role");
		personRoleField.attr("name", creator+"s[" + creatorSubmitCount + "].role");
		personRoleField.attr("value", role);
		$("#editForm").append(personRoleField);
		
		var personUriField = $("<input>");
		personUriField.attr("type", "hidden");
		personUriField.attr("id", creator+"s" + creatorSubmitCount + ".uri");
		personUriField.attr("name", creator+"s[" + creatorSubmitCount + "].uri");
		personUriField.attr("value", $(person).attr("data-"+arrayName+"-uri"));
		$("#editForm").append(personUriField);
		
		var personAuthorityField = $("<input>");
		personAuthorityField.attr("type", "hidden");
		personAuthorityField.attr("id", creator+"s" + creatorSubmitCount + ".localAuthorityId");
		personAuthorityField.attr("name", creator+"s[" + creatorSubmitCount + "].localAuthorityId");
		
		personAuthorityField.attr("value", $(person).attr("data-"+arrayName+"-authority-id"));
		$("#editForm").append(personAuthorityField);
		
		$(person).children(".affiliation-class").each(function(idx2, affiliation) {
			var affiliationField = $("<input>");
			affiliationField.attr("type", "hidden");
			affiliationField.attr("id", creator+"s" + creatorSubmitCount + ".affiliations" + idx2 + ".name");
			affiliationField.attr("name", creator+"s[" + creatorSubmitCount + "].affiliations[" + idx2 + "].name");
			affiliationField.attr("value", $(affiliation).attr("data-affiliation-name"));
			$("#editForm").append(affiliationField);
			
			var affiliationIdField = $("<input>");
			affiliationIdField.attr("type", "hidden");
			affiliationIdField.attr("id", creator+"s" + creatorSubmitCount + ".affiliations" + idx2 + ".id");
			affiliationIdField.attr("name", creator+"s[" + creatorSubmitCount + "].affiliations[" + idx2 + "].id");
			affiliationIdField.attr("value", $(affiliation).attr("data-affiliation-id"));
			$("#editForm").append(affiliationIdField);
			
			var affiliationUriField = $("<input>");
			affiliationUriField.attr("type", "hidden");
			affiliationUriField.attr("id", creator+"s" + creatorSubmitCount + ".affiliations" + idx2 + ".uri");
			affiliationUriField.attr("name", creator+"s[" + creatorSubmitCount + "].affiliations[" + idx2 + "].uri");
			affiliationUriField.attr("value", $(affiliation).attr("data-affiliation-uri"));
			$("#editForm").append(affiliationUriField);
		});
		otherCreatorCount += 1;
	});
	return otherCreatorCount;
}

function createConceptTags() {
	$("#conceptTags").children("span").each(function (idx, tag) {
		var conceptTagInput = $("<input>");
		conceptTagInput.attr("type", "hidden");
		conceptTagInput.attr("id", "conceptTags" + idx + ".conceptId");
		conceptTagInput.attr("name", "conceptTags[" + idx + "].conceptId");
		conceptTagInput.attr("value", $(tag).attr("data-concept-id"));
		$("#editForm").append(conceptTagInput);
		
		var conceptTagTypeInput = $("<input>");
		conceptTagTypeInput.attr("type", "hidden");
		conceptTagTypeInput.attr("id", "conceptTags" + idx + ".conceptTypeId");
		conceptTagTypeInput.attr("name", "conceptTags[" + idx + "].conceptTypeId");
		conceptTagTypeInput.attr("value", $(tag).attr("data-concept-type-id"));
		$("#editForm").append(conceptTagTypeInput);
		
		var conceptTagUri = $("<input>");
		conceptTagUri.attr("type", "hidden");
		conceptTagUri.attr("id", "conceptTags" + idx + ".conceptUri");
		conceptTagUri.attr("name", "conceptTags[" + idx + "].conceptUri");
		conceptTagUri.attr("value", $(tag).attr("data-concept-uri"));
		$("#editForm").append(conceptTagUri);
		
		var conceptTagName = $("<input>");
		conceptTagName.attr("type", "hidden");
		conceptTagName.attr("id", "conceptTags" + idx + ".conceptName");
		conceptTagName.attr("name", "conceptTags[" + idx + "].conceptName");
		conceptTagName.attr("value", $(tag).attr("data-concept-name"));
		$("#editForm").append(conceptTagName);
		
		var conceptTagType = $("<input>");
		conceptTagType.attr("type", "hidden");
		conceptTagType.attr("id", "conceptTags" + idx + ".conceptTypeName");
		conceptTagType.attr("name", "conceptTags[" + idx + "].conceptTypeName");
		conceptTagType.attr("value", $(tag).attr("data-type-name"));
		$("#editForm").append(conceptTagType);
		
		var conceptTagTypeUri = $("<input>");
		conceptTagTypeUri.attr("type", "hidden");
		conceptTagTypeUri.attr("id", "conceptTags" + idx + ".conceptTypeUri");
		conceptTagTypeUri.attr("name", "conceptTags[" + idx + "].conceptTypeUri");
		conceptTagTypeUri.attr("value", $(tag).attr("data-type-uri"));
		$("#editForm").append(conceptTagTypeUri);
	});
}

function resetPersonCreationModal(modalType) {
	var modalNameLCase = modalType.toLowerCase();
	$("#firstName"+modalType).val("");
	$("#lastName"+modalType).val("");
	$("#id"+modalType).attr("data-"+modalType+"-id", "");
	$("#"+modalNameLCase+"Affiliations").children().remove();
	$("#uri"+modalType).val("");
	if(modalType == "Creator") {
		$("#addCreatorButton").attr("data-creator-type", "");
		$("#creatorLabel").text("Enter Creator Information");
		$("#addCreatorButton").text("Add Creator");
	}
	
	$("#add"+modalType+"Button").text("Add "+modalType);
	$("#add"+modalType+"Button").prop("disabled",true);
	resetPersonAuthorityCreation(modalType);
	affCount = 0;
}

function resetPersonAuthorityCreation(personType) {
	$("#uriLoadingFound"+personType).hide();
	$("#uriLoadingFailure"+personType).hide();
	$("#uriLoadingSpinner"+personType).hide();
	$("#uriLoadingFound"+personType).popover('hide');
	$("#uriLoadingFailure"+personType).popover('hide');
	$("#"+personType.toLowerCase()+"AuthorityUsed").html("");
}

function getPersonAuthority(uri, personType) {
	
	personType_lowerCase = personType.toLowerCase();
	$.get("/auth/authority/get?uri=" + uri + '&zoteroGroupId=' + zoteroGroupId, function(data) {
		$("#uriLoadingFound"+personType).attr("data-authority-uri", data['uri']);
		var content = "Authority <b>" + uri + "</b>";
		if (data['userAuthorityEntries'] != null && data['userAuthorityEntries'].length > 0) {
			content += "<br><br>This authority entry has already been imported by you:";
			content += '<ul class="foundAuthorities">';
			data['userAuthorityEntries'].forEach(function(elem) {
				content += '<li>' + elem['name'];
				content += ' [<a href="" data-authority-id="' + elem['id'] + '" data-authority-name="' + elem['name'] + '">Use this one</a>]';
				content += '</li>';
			});
			content += "</ul>";
		}
		if (data['datasetAuthorityEntries'] != null && data['datasetAuthorityEntries'].length > 0) {
			content += "<br><br>This authority entry has already been imported by someone else for this dataset:";
			content += '<ul class="foundAuthorities">';
			data['datasetAuthorityEntries'].forEach(function(elem) {
				content += '<li>' + elem['name'];
				content += ' [<a href="" data-authority-id="' + elem['id'] + '" data-authority-name="' + elem['name'] + '">Use this one</a>]';
				content += '</li>';
			});
			content += "</ul>";
		}
		
		if((data['userAuthorityEntries'] == null || data['userAuthorityEntries'].length == 0) 
				&& (data['datasetAuthorityEntries'] == null || data['datasetAuthorityEntries'].length == 0)) {
			content += "<br><br>No authorities found for the given URI<br>";
		}
		
		$("#uriLoadingFound"+personType).attr("data-content", content);
		$("#uriLoadingFound"+personType).attr("data-authority-uri", uri);
		$("#uriLoadingFound"+personType).show();
		$("#uriLoadingFound"+personType).popover('show');
	})
	.fail(function() {
		$("#uriLoadingFailure"+personType).show();
	 })
    .always(function() {
    	$("#uriLoadingSpinner"+personType).hide();
	 });

}


function getUserAuthorities(modalType, personType, page) {
	var firstName = $("#firstName"+personType).val();
	var lastName = $("#lastName"+personType).val();
	console.log("getUserAuthorities")
	if (lastName === undefined) {
	    lastName = "";
	}
	personType_lowerCase = personType.toLowerCase();
	url = "/auth/authority/"+zoteroGroupId+ "/find/authorities/user" + '?firstName='+ firstName + '&lastName=' + lastName +'&page='+page;
	$.ajax({
  		dataType: "json",
  		type: 'GET',
  		url: url ,
  		async: false,
  		success: function(data) {
			
  			$("#userAuthoritySearchResult").empty();
  			var content = '';
  			
  			if (data['foundAuthorities'] != null && data['foundAuthorities'].length > 0) {
  				data['foundAuthorities'].forEach(function(elem) {
  					content += '<tr> <td class="name">' + elem['name'] + '</td> <td class="uri">' + elem['uri'] + '</td> <td>' ;
  					if(elem['description']==null){
  						content += ' - </td>';
  						}
  					else{
  						content +=elem['description'] + '</td>';
  					}
  					content += '<td style="vertical-align: middle;"><span class="user-authority-entry btn btn-primary" title="Use Authority" style="padding: 5px;"><i class="icon-checkmark-alt" style="color: white;"></i></span></td></tr>';
  				});
  				
  				$('#userAuthority-pagination-top').twbsPagination({
  				    totalPages: data['totalPages'],
  				    startPage: data['currentPage'],
  				    prev: "«",
  				    next: "»",
  				    visiblePages: 5,
  				    initiateStartPageClick: false,
  				    onPageClick:function(event, page) {
  				    	   getUserAuthorities(modalType, personType, page-1)
  				    }
  				});
  			}  		
  			
		
		$("#userAuthoritySearchResult").append(content);
		$(".user-authority-entry").click(function() {
			name = $(this).closest("tr").find(".name").text();
			uri = $(this).closest("tr").find(".uri").text();
			
			if (modalType.includes("Affiliation")) {
			    showAffiliationNameInModal(name, uri, personType);
			} else {
			    showPersonNameInModal(name, personType)
			    $("#uri"+modalType).val( uri);
			}
			
			$("#uri"+modalType).val( uri);
			$("#"+personType_lowerCase+"AuthorityUsed").html("Using stored authority entry <i>" + name + "</i>.");
			$("#selectAuthorityModel").modal('hide');
		});	
		 	
        },
    	error: function(data){
    		$('#userAuthoritySearchResult').parents('table').hide()
    		$("#userAuthoritiesError").show();	
    	}
	
	});
}

function getGroupAuthorities(modalType, personType, page) {
	var firstName = $("#firstName"+personType).val();
	var lastName = $("#lastName"+personType).val();
	if (lastName === undefined) {
	    lastName = "";
	}
	personType_lowerCase = personType.toLowerCase();
	url = "/auth/authority/"+ zoteroGroupId + "/find/authorities/group" + '?firstName='+ firstName + '&lastName=' + lastName +'&page='+page;
	$.ajax({
  		dataType: "json",
  		type: 'GET',
  		url: url ,
  		async: false,
  		success: function(data) {
			
  			$("#groupAuthoritySearchResult").empty();
  			var content = '';
  			
  			if (data['foundAuthorities'] != null && data['foundAuthorities'].length > 0) {
  				data['foundAuthorities'].forEach(function(elem) {
  					content += '<tr> <td class="name">' + elem['name'] + '</td> <td class="uri">' + elem['uri'] + '</td> <td>' ;
  					if(elem['description']==null){
  						content += ' - </td>';
  						}
  					else{
  						content +=elem['description'] + '</td>';
  					}
  					content += '<td style="vertical-align: middle;"><span class="group-authority-entry btn btn-primary" title="Use Authority" style="padding: 5px;"><i class="icon-checkmark-alt" style="color: white;"></i></span></td></tr>';
  				});
  				
  				$('#groupAuthority-pagination-top').twbsPagination({
  				    totalPages: data['totalPages'],
  				    startPage: data['currentPage'],
  				    prev: "«",
  				    next: "»",
  				    visiblePages: 5,
  				    initiateStartPageClick: false,
  				    onPageClick:function(event, page) {
  				    	getGroupAuthorities(modalType, personType, page-1)
  				    }
  				});
  			}  		
  			
		
		$("#groupAuthoritySearchResult").append(content);
		$(".group-authority-entry").click(function() {
			name = $(this).closest("tr").find(".name").text();
			uri = $(this).closest("tr").find(".uri").text();
			
			if (modalType.includes("Affiliation")) {
			    showAffiliationNameInModal(name, uri, personType);
			} else {
			    showPersonNameInModal(name, personType)
			    $("#uri"+modalType).val( uri);
			}
			showPersonNameInModal(name, personType)
			$("#uri"+modalType).val( uri);
			$("#"+personType_lowerCase+"AuthorityUsed").html("Using stored authority entry <i>" + name + "</i>.");
			$("#selectAuthorityModel").modal('hide');
		});	
		 	
        },
    	error: function(data){
    		$('#groupAuthoritySearchResult').parents('table').hide()
    		$("#groupAuthoritiesError").show();	
    	}
	
	});
}

function getconceptpowerAuthorities(modalType, personType, page) {
	var firstName = $("#firstName"+personType).val();
	var lastName = $("#lastName"+personType).val();
	if (lastName === undefined) {
	    lastName = "";
	}
	personType_lowerCase = personType.toLowerCase();
	url = "/auth/authority/"+ zoteroGroupId + "/find/authorities/conceptpower" + '?firstName='+ firstName + '&lastName=' + lastName +'&page='+page;		

	$.ajax({
  		dataType: "json",
  		type: 'GET',
  		url: url ,
  		async: false,
  		success: function(data) {
  			$('#cp-groupName').text(data['groupName']);
  			$("#conceptpowerAuthoritySearchResult").empty();
  			var content = '';
  				
  			if (data['foundAuthorities'] != null && data['foundAuthorities'].length > 0) {
  				data['foundAuthorities'].forEach(function(elem) {	
  					content += '<tr> <td class="name">' + elem['name'] + '</td> <td class="uri">' + elem['uri'] + '</td> <td>' ;
  					if(elem['description']==null){
  						content += ' - </td>';
  						}
  					else{
  						content +=elem['description'] + '</td>';
  					}
  					content += '<td style="vertical-align: middle;"><span class="conceptpower-authority-entry btn btn-primary" title="Create new managed authority" style="padding: 5px;"><i class="icon-checkmark-alt" style="color: white;"></i></span></td></tr>';
  					
  				}); 				
  				
  				$('#conceptpowerAuthority-pagination-top').twbsPagination({
  				    totalPages: data['totalPages'],
  				    startPage: data['currentPage'],
  				    prev: "«",
  				    next: "»",
  				    visiblePages: 5,
  				    initiateStartPageClick: false,
  				    onPageClick:function(event, page) {
  				    	getconceptpowerAuthorities(modalType, personType, page)
  				    }
  				});
  				
  			}
  			
		$("#conceptpowerAuthoritySearchResult").append(content); 	
		$(".conceptpower-authority-entry").click(function() {
			name = $(this).closest("tr").find(".name").text();
			uri = $(this).closest("tr").find(".uri").text();
			
			if (modalType.includes("Affiliation")) {
			    showAffiliationNameInModal(name, uri, personType);
			} else {
			    showPersonNameInModal(name, personType)
			    $("#uri"+modalType).val( uri);
			}
			
			createManageAuthorityURL = "/auth/authority/add?"+ '&source=conceptpower&uri=' + uri;	
			if($("#cp-checkbox").is(":checked")){
				createManageAuthorityURL += '&zoteroGroupId=' + zoteroGroupId;						
			}
			$.ajax({
			  		dataType: "json",
			  		type: 'POST',
			  		url: createManageAuthorityURL,
			  		data: { csrfParameterName : csrfToken },
			  		async:false,
			  		success: function(data) {
			  			$("#"+personType_lowerCase+"AuthorityUsed").html("Created new authority entry <i>" + name + "</i>.");
			  		},
				error: function(data){					
					$("#uri"+modalType).val("");
		  			$("#"+personType_lowerCase+"AuthorityUsed").html("Failed to create new authority entry <i>" + name + "</i>.");
				}
				});				
						
			$("#selectAuthorityModel").modal('hide');
		});	
		
        },
	error: function(data){
		$('#conceptpowerAuthoritySearchResult').parents('table').hide()
		$("#conceptpowerAuthoritiesError").show();	
	}
	
	});
}

function getViafAuthorities(modalType, personType, page) {
	var firstName = $("#firstName"+personType).val();
	var lastName = $("#lastName"+personType).val();
	if (lastName === undefined) {
	    lastName = "";
	}
	
	personType_lowerCase = personType.toLowerCase();
	url = "/auth/authority/"+ zoteroGroupId +"/find/authorities/viaf" + '?firstName='+ firstName + '&lastName=' + lastName +'&page='+page;		

	$.ajax({
  		dataType: "json",
  		type: 'GET',
  		url: url ,
  		async: false,
  		success: function(data) {
  			$('#viaf-groupName').text(data['groupName']);
  			$("#viafAuthoritySearchResult").empty();
  			var content = '';
  				
  			if (data['foundAuthorities'] != null && data['foundAuthorities'].length > 0) {
  				data['foundAuthorities'].forEach(function(elem) {	
  					content += '<tr> <td class="name">' + elem['name'] + '</td> <td class="uri">' + elem['uri'] + '</td> <td>' ;
  					if(elem['description']==null) {
  						content += ' - </td>';
  					}
  					else {
  						content +=elem['description'] + '</td>';
  					}
  					content += '<td style="vertical-align: middle;"><span class="viaf-authority-entry btn btn-primary" title="Create new managed authority" style="padding: 5px;"><i class="icon-checkmark-alt" style="color: white;"></i></span></td></tr>';
  					
  				}); 				
  				
  				$('#viafAuthority-pagination-top').twbsPagination({
  				    totalPages: data['totalPages'],
  				    startPage: data['currentPage'],
  				    prev: "«",
  				    next: "»",
  				    visiblePages: 5,
  				    initiateStartPageClick: false,
  				    onPageClick:function(event, page) {
  				    	getViafAuthorities(modalType, personType, page-1)
  				    }
  				});
  				
  			}
  			
		$("#viafAuthoritySearchResult").append(content); 	
		$(".viaf-authority-entry").click(function() {
			name = $(this).closest("tr").find(".name").text();
			uri = $(this).closest("tr").find(".uri").text();
			
			if (modalType.includes("Affiliation")) {
			    showAffiliationNameInModal(name, uri, personType);
			} else {
			    showPersonNameInModal(name, personType)
			    $("#uri"+modalType).val( uri);
			}
			
			createManageAuthorityURL = "/auth/authority/add?"+ '&source=viaf&uri=' + uri;	
			if($("#viaf-checkbox").is(":checked")) {
				createManageAuthorityURL += '&zoteroGroupId=' + zoteroGroupId;						
			}
			$.ajax({
				dataType: "json",
				type: 'POST',
				url: createManageAuthorityURL,
				data: { csrfParameterName : csrfToken },
				async:false,
				success: function(data) {
					$("#"+personType_lowerCase+"AuthorityUsed").html("Created new authority entry <i>" + name + "</i>.");
				},
				error: function(data) {					
					$("#uri"+modalType).val("");
		  			$("#"+personType_lowerCase+"AuthorityUsed").html("Failed to create new authority entry <i>" + name + "</i>.");
				}
			});				
						
			$("#selectAuthorityModel").modal('hide');
		});	
		
        },
	error: function(data){
		$('#viafAuthoritySearchResult').parents('table').hide()
		$("#viafAuthoritiesError").show();	
	}
	
	});
}

let removePerson = function removePerson(e) {
	var deleteIcon = e.currentTarget;
	var person = $(deleteIcon).parent();
	person.remove();
}

let removeConcept = function removeConcept(e) {
	var deleteIcon = e.currentTarget;
	var concept = $(deleteIcon).parent();
	concept.remove();
}

// $(document).ready(function() {
  
	
// });

function loadFields() {
	var itemType = $('#items option:selected').val()
	$("#displayMessage").html("<i class='glyphicon glyphicon-refresh spinning'></i>" +
		" Loading form fields");
	$("#messageModal").modal('show');
	$.ajax({
		url : "/auth/items/" + itemType + '/fields',
		type : 'GET',
		success: function(changedFields){
			$('form input').each(function(idx, elem) {
				$(elem).parent().closest('tr').hide();
			});
			for(i=0;i<changedFields.length;i++){
				var fieldId = changedFields[i];
				if (fieldId == "date") {
					fieldId = "dateFreetext";
				}
				$('form input#'+fieldId).parent().closest('tr').show();
			}
			$('#messageModal').modal('hide');
			
		},
		error: function(){
			$("#displayMessage").html("<i class='glyphicon glyphicon-remove-sign'></i>" +
			"Error loading the form fields. Try again later.");
			$('#messageModal').modal('show');
			setTimeout(function() {
				$('#messageModal').modal('hide');
		  	}, 3000);
			
		}
	}); 
	$.ajax({
		url : "/auth/items/" + itemType + '/creators',
		
		type : 'GET',
		success: function(creators){
			$('.creator-row').each(function(idx, elem) {
				$(elem).hide();
			});
			for(i=0;i<creators.length;i++){
				if($('[id='+creators[i]).length > 0) {
					$('[id='+creators[i]).parent().closest('tr').addClass("creator-row");
					$('[id='+creators[i]).parent().closest('tr').show();
				}
				else if(creators[i]!= 'editor' && creators[i]!= 'author'){
					var creatorRow = $("<tr>");
					creatorRow.css("display", "table-row");
					creatorRow.addClass("creator-row");
					var creatorLabel = $("<td>");
					creatorLabel.addClass("creator");
					creatorLabel.css("text-transform", "capitalize");
					creatorLabel.attr("id", creators[i]);
					creatorLabel.append(creators[i]);
					creatorRow.append(creatorLabel);
					var creatorData = $("<td>");
					var creatorList = $("<span>");
					creatorList.attr("id",creators[i]+"List");
					creatorList.css("font-size", "18px");
					creatorData.append(creatorList);
					var addIconDiv = $("<div>");
					addIconDiv.addClass("pull-right");
					var iconLink = $("<a>");
					
					iconLink.attr("data-toggle","modal");
					iconLink.attr("data-creator-type", creators[i]);
					iconLink.attr("data-target","#creatorModal");
					var iconImg = $("<i>");
					iconImg.addClass("icon-circle-add");
					iconLink.append(iconImg);
					iconLink.append("Add "+creators[i]);
					addIconDiv.append(iconLink);
					creatorData.append(addIconDiv);
					creatorRow.append(creatorData);
					creatorRow.insertAfter($('.creator').last().parent());	
					$("#creatorLabel").css("text-transform", "capitalize");
					$("#creatorLabel").text("Enter "+creators[i]+" Information");
					$("#addCreatorButton").css("text-transform", "capitalize");
					$("#addCreatorButton").text("Add "+creators[i]);
					$("#addCreatorButton").attr("data-creator-type", creators[i]);
					iconLink.click(function(e) {
						creatorLinkHandler($(e.target));
					});
				}

			}
		},
		error: function(){
			$("#displayMessage").html("<i class='glyphicon glyphicon-remove-sign'></i>" +
			"Error loading the creators. Try again later.");
			$('#messageModal').modal('show');
			setTimeout(function() {
				$('#messageModal').modal('hide');
		  	}, 3000);
			
		}
	});
}
function creatorLinkHandler(target) {
	var creatorType = target.attr("data-creator-type").charAt(0).toUpperCase() + target.attr("data-creator-type").slice(1);
	$("#creatorLabel").text("Enter "+creatorType+" Information");
	$("#addCreatorButton").text("Add "+creatorType);
	$("#addCreatorButton").attr("data-creator-type", target.attr("data-creator-type"));
}



