


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
	$("#search"+modalType+"Spinner").hide();
}


function getUserAuthorities(modalType, personType, page) {
	var firstName = $("#firstName"+personType).val();
	var lastName = $("#lastName"+personType).val();
	if (lastName === undefined) {
	    lastName = "";
	}
	personType_lowerCase = personType.toLowerCase();
	url = [[@{|/auth/authority/${zoteroGroupId}/find/authorities/user|}]] + '?firstName='+ firstName + '&lastName=' + lastName +'&page='+page;
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
	url = [[@{|/auth/authority/${zoteroGroupId}/find/authorities/group|}]] + '?firstName='+ firstName + '&lastName=' + lastName +'&page='+page;
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
	url = [[@{|/auth/authority/${zoteroGroupId}/find/authorities/conceptpower|}]] + '?firstName='+ firstName + '&lastName=' + lastName +'&page='+page;		

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
			
			createManageAuthorityURL = [[@{/auth/authority/add?}]]+ '&source=conceptpower&uri=' + uri;	
			if($("#cp-checkbox").is(":checked")){
				createManageAuthorityURL += '&zoteroGroupId=' + [(${zoteroGroupId})];						
			}
			$.ajax({
			  		dataType: "json",
			  		type: 'POST',
			  		url: createManageAuthorityURL,
			  		data: { [[${_csrf.parameterName}]] : [[${_csrf.token}]] },
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
	url = [[@{|/auth/authority/${zoteroGroupId}/find/authorities/viaf|}]] + '?firstName='+ firstName + '&lastName=' + lastName +'&page='+page;		

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
			
			createManageAuthorityURL = [[@{/auth/authority/add?}]]+ '&source=viaf&uri=' + uri;	
			if($("#viaf-checkbox").is(":checked")) {
				createManageAuthorityURL += '&zoteroGroupId=' + [(${zoteroGroupId})];						
			}
			$.ajax({
				dataType: "json",
				type: 'POST',
				url: createManageAuthorityURL,
				data: { [[${_csrf.parameterName}]] : [[${_csrf.token}]] },
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


$("#searchAuthor").click(function() {
    searchAuthorities('Author', 'Author');
});

$("#searchEditor").click(function() {
    searchAuthorities('Editor', 'Editor');
});

$("#searchCreator").click(function() {
    searchAuthorities('Creator', 'Creator');
}); 
