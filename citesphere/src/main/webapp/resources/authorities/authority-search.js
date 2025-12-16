var CitesphereAuthority = CitesphereAuthority || {};

CitesphereAuthority.Config = (function() {
    var config = {
        baseUrl: '',
        zoteroGroupId: null,
        csrfToken: null,
        csrfParameterName: null,
        endpoints: {},
        importerNames: {}
    };

    return {
        setBaseUrl: function(url) { config.baseUrl = url; },
        getBaseUrl: function() { return config.baseUrl; },
        setZoteroGroupId: function(groupId) { config.zoteroGroupId = groupId; },
        getZoteroGroupId: function() { return config.zoteroGroupId; },
        setCsrfToken: function(token) { config.csrfToken = token; },
        getCsrfToken: function() { return config.csrfToken; },
        setCsrfParameterName: function(name) { config.csrfParameterName = name; },
        getCsrfParameterName: function() { return config.csrfParameterName; },
        setEndpoint: function(key, url) { config.endpoints[key] = url; },
        getEndpoint: function(key) {
            var endpoint = config.endpoints[key] || '';
            return config.baseUrl + endpoint;
        },
        setImporterName: function(key, name) { config.importerNames[key] = name; },
        getImporterName: function(key) { return config.importerNames[key] || ''; },
        setImporterId: function(key, id) {
            config.importerIds = config.importerIds || {};
            config.importerIds[key] = id;
        },
        getImporterId: function(key) { return (config.importerIds && config.importerIds[key]) || ''; }
    };
})();

CitesphereAuthority.BaseConnector = function(options) {
    this.source = options.source;
    this.endpointKey = options.endpointKey || options.source;
    this.importerId = options.importerId || null;
    this.importerName = options.importerName || null;
    this.useGroupId = options.useGroupId || false;
};

CitesphereAuthority.BaseConnector.prototype = {
    buildUrl: function(params) {
        var url = CitesphereAuthority.Config.getEndpoint(this.endpointKey);

        if (this.useGroupId) {
            var groupId = params.groupId || CitesphereAuthority.Config.getZoteroGroupId();
            url = url.replace('{groupId}', groupId);
        }

        var queryParams = {
            firstName: params.firstName || '',
            lastName: params.lastName || '',
            page: params.page || 0
        };

        var queryString = $.param(queryParams);
        return url + (url.indexOf('?') === -1 ? '?' : '&') + queryString;
    },

    search: function(params, callbacks) {
        var self = this;
        var url = this.buildUrl(params);

        $.ajax({
            dataType: "json",
            type: 'GET',
            url: url,
            success: function(data) {
                var results = self.processResults(data);
                if (callbacks.onSuccess) {
                    callbacks.onSuccess(results);
                }
            },
            error: function(xhr, status, error) {
                if (callbacks.onError) {
                    callbacks.onError({xhr: xhr, status: status, error: error});
                }
            }
        });
    },

    processResults: function(data) {
        return {
            authorities: data.foundAuthorities || [],
            totalPages: data.totalPages || 0,
            currentPage: data.currentPage || 1,
            source: this.source,
            importerId: this.getImporterId(),
            importerName: this.getImporterName()
        };
    },

    getImporterId: function() {
        return this.importerId || CitesphereAuthority.Config.getImporterId(this.source);
    },

    getImporterName: function() {
        return this.importerName || CitesphereAuthority.Config.getImporterName(this.source);
    }
};

CitesphereAuthority.ConceptpowerConnector = function(options) {
    options = options || {};
    options.source = 'conceptpower';
    options.importerId = options.importerId || 'authority.importer.conceptpower';
    CitesphereAuthority.BaseConnector.call(this, options);
};
CitesphereAuthority.ConceptpowerConnector.prototype = Object.create(CitesphereAuthority.BaseConnector.prototype);
CitesphereAuthority.ConceptpowerConnector.prototype.constructor = CitesphereAuthority.ConceptpowerConnector;

CitesphereAuthority.ViafConnector = function(options) {
    options = options || {};
    options.source = 'viaf';
    options.importerId = options.importerId || 'authority.importer.viaf';
    CitesphereAuthority.BaseConnector.call(this, options);
};
CitesphereAuthority.ViafConnector.prototype = Object.create(CitesphereAuthority.BaseConnector.prototype);
CitesphereAuthority.ViafConnector.prototype.constructor = CitesphereAuthority.ViafConnector;

CitesphereAuthority.IsiscbConnector = function(options) {
    options = options || {};
    options.source = 'isiscb';
    CitesphereAuthority.BaseConnector.call(this, options);
};
CitesphereAuthority.IsiscbConnector.prototype = Object.create(CitesphereAuthority.BaseConnector.prototype);
CitesphereAuthority.IsiscbConnector.prototype.constructor = CitesphereAuthority.IsiscbConnector;

CitesphereAuthority.UserConnector = function(options) {
    options = options || {};
    options.source = 'user';
    options.useGroupId = true;
    CitesphereAuthority.BaseConnector.call(this, options);
};
CitesphereAuthority.UserConnector.prototype = Object.create(CitesphereAuthority.BaseConnector.prototype);
CitesphereAuthority.UserConnector.prototype.constructor = CitesphereAuthority.UserConnector;

CitesphereAuthority.GroupConnector = function(options) {
    options = options || {};
    options.source = 'group';
    options.useGroupId = true;
    CitesphereAuthority.BaseConnector.call(this, options);
};
CitesphereAuthority.GroupConnector.prototype = Object.create(CitesphereAuthority.BaseConnector.prototype);
CitesphereAuthority.GroupConnector.prototype.constructor = CitesphereAuthority.GroupConnector;

CitesphereAuthority.SearchResults = function(options) {
    this.containerId = options.containerId;
    this.source = options.source;
    this.onSelect = options.onSelect || function() {};
    this.showImporterInfo = options.showImporterInfo !== false;
};

CitesphereAuthority.SearchResults.prototype = {
    render: function(results) {
        var $container = $('#' + this.containerId);
        $container.empty();

        if (!results.authorities || results.authorities.length === 0) {
            return;
        }

        var self = this;
        var content = '';

        results.authorities.forEach(function(authority) {
            content += self.renderRow(authority, results);
        });

        $container.append(content);
        this.bindEvents(results);
    },

    renderRow: function(authority, results) {
        var content = '<tr>';
        content += '<td class="name">' + (authority.name || '') + '</td>';
        content += '<td class="uri">' + (authority.uri || '') + '</td>';
        content += '<td class="description">' + (authority.description || ' - ') + '</td>';

        if (this.showImporterInfo && results.importerName) {
            content += '<td style="display: none;" class="importerName">' + results.importerName + '</td>';
        }
        if (this.showImporterInfo && results.importerId) {
            content += '<td style="display: none;" class="importerId">' + results.importerId + '</td>';
        }

        content += '<td style="vertical-align: middle;">';
        content += '<span class="' + this.source + '-authority-entry btn btn-primary" ';
        content += 'title="Use Authority" style="padding: 5px;">';
        content += '<i class="icon-checkmark-alt" style="color: white;"></i></span></td>';
        content += '</tr>';

        return content;
    },

    bindEvents: function(results) {
        var self = this;

        $('.' + this.source + '-authority-entry').off('click').on('click', function() {
            var $row = $(this).closest('tr');
            var authority = {
                name: $row.find('.name').text(),
                uri: $row.find('.uri').text(),
                description: $row.find('.description').text(),
                importerId: $row.find('.importerId').text() || results.importerId,
                importerName: $row.find('.importerName').text() || results.importerName,
                source: self.source
            };
            self.onSelect(authority);
        });
    },

    showError: function() {
        var $container = $('#' + this.containerId);
        $container.parents('table').hide();
        $('#' + this.source + 'AuthoritiesError').show();
    }
};

CitesphereAuthority.Pagination = function(options) {
    this.containerId = options.containerId;
    this.onPageChange = options.onPageChange || function() {};
};

CitesphereAuthority.Pagination.prototype = {
    render: function(results) {
        var self = this;
        var $container = $('#' + this.containerId);

        this.destroy();

        if (results.totalPages > 0) {
            $container.twbsPagination({
                totalPages: results.totalPages,
                startPage: results.currentPage,
                prev: "«",
                next: "»",
                visiblePages: 5,
                initiateStartPageClick: false,
                onPageClick: function(event, page) {
                    self.onPageChange(page - 1);
                }
            });
        }
    },

    destroy: function() {
        var $container = $('#' + this.containerId);
        try {
            $container.twbsPagination('destroy');
        } catch (e) {
            // Ignore if not initialized
        }
    }
};

CitesphereAuthority.AuthorityService = function() {
    this.connectors = {};
};

CitesphereAuthority.AuthorityService.prototype = {
    registerConnector: function(name, connector) {
        this.connectors[name] = connector;
    },

    getConnector: function(name) {
        return this.connectors[name];
    },

    search: function(source, params, callbacks) {
        var connector = this.connectors[source];
        if (connector) {
            connector.search(params, callbacks);
        }
    },

    searchAll: function(sources, params, callbacks) {
        var self = this;

        sources.forEach(function(source) {
            self.search(source, params, {
                onSuccess: function(results) {
                    if (callbacks.onSourceSuccess) {
                        callbacks.onSourceSuccess(source, results);
                    }
                },
                onError: function(error) {
                    if (callbacks.onSourceError) {
                        callbacks.onSourceError(source, error);
                    }
                }
            });
        });
    }
};

CitesphereAuthority.CreateHandler = function(options) {
    this.options = options || {};
    this.authorityService = new CitesphereAuthority.AuthorityService();
    this.searchResults = {};
    this.pagination = {};
    this.sources = options.sources || ['conceptpower', 'viaf', 'isiscb'];

    this._initConnectors();
    this._initComponents();
};

CitesphereAuthority.CreateHandler.prototype = {
    _initConnectors: function() {
        var self = this;

        this.sources.forEach(function(source) {
            var ConnectorClass;
            switch (source) {
                case 'conceptpower':
                    ConnectorClass = CitesphereAuthority.ConceptpowerConnector;
                    break;
                case 'viaf':
                    ConnectorClass = CitesphereAuthority.ViafConnector;
                    break;
                case 'isiscb':
                    ConnectorClass = CitesphereAuthority.IsiscbConnector;
                    break;
                default:
                    return;
            }
            self.authorityService.registerConnector(source, new ConnectorClass());
        });
    },

    _initComponents: function() {
        var self = this;

        this.sources.forEach(function(source) {
            self.searchResults[source] = new CitesphereAuthority.SearchResults({
                containerId: source + 'AuthoritySearchResult',
                source: source,
                onSelect: function(authority) {
                    self._onAuthoritySelected(authority);
                }
            });

            self.pagination[source] = new CitesphereAuthority.Pagination({
                containerId: source + 'Authority-pagination-top',
                onPageChange: function(page) {
                    self._onPageChange(source, page);
                }
            });
        });
    },

    init: function() {
        var self = this;

        this._hideLoadingIndicators();

        $('#firstNameAuthor, #lastNameAuthor').on('keyup', function() {
            self._updateSearchButtonState();
        });

        $('#searchAuthor').on('click', function() {
            self._onSearchClick();
        });

        $('#closeAuthoritySearchResult').on('click', function() {
            $('#selectAuthorityModel').modal('hide');
            $('#selectAuthorityModel a:first').tab('show');
        });

        $('#addAuthorModalCancel').on('click', function() {
            $('#authorModal').modal('hide');
            self._resetModal();
        });

        $('#clear').on('click', function() {
            $('#tr-uriAuthor').hide();
            $('#tr-importerId').hide();
            $('#tr-importerName').hide();
        });

        var uriTimer = null;
        $('#uriAuthor').on('change', function() {
            self._resetAuthorityCreation();
            $('#uriLoadingSpinnerAuthor').show();
            var uri = $(this).val();
            clearTimeout(uriTimer);
            uriTimer = setTimeout(function() {
                self._getPersonAuthority(uri);
            }, 1000);
        });

        $('#authorIconContainer').on('click', '.popover #authorCreateAuthority', function() {
            var name = $(this).closest('div').find('.name').text();
            var uri = $(this).closest('div').find('.uri').text();
            var importerId = $(this).closest('div').find('.importerId').text();
            var importerName = $(this).closest('div').find('.importerName').text();

            self._showPerson(name, null, uri, importerId, importerName);
            $('#authorCreateAuthority').hide();
            $('#uriLoadingFoundAuthor').popover('hide');
            $('#selectAuthorityModel').modal('hide');
            $('#authorModal').modal('hide');
            self._resetModal();
        });
    },

    _hideLoadingIndicators: function() {
        $('#uriLoadingSpinnerAuthor').hide();
        $('#uriLoadingFailureAuthor').hide();
        $('#uriLoadingFoundAuthor').hide();
        $('#searchAuthorSpinner').hide();
        $('#tr-uriAuthor').hide();
        $('#tr-importerId').hide();
        $('#tr-importerName').hide();
        $('#importerId').hide();
        $('#importerName').hide();
    },

    _updateSearchButtonState: function() {
        var firstName = $('#firstNameAuthor').val();
        var lastName = $('#lastNameAuthor').val();
        var disabled = (firstName === '' && lastName === '');
        $('#searchAuthor').prop('disabled', disabled);
    },

    _onSearchClick: function() {
        var self = this;

        $('#selectAuthorityModel').on('hidden.bs.modal', function() {
            $('#selectAuthorityModel a:first').tab('show');
        });

        $('#searchAuthorSpinner').show();

        this.sources.forEach(function(source) {
            self.pagination[source].destroy();
        });

        var params = {
            firstName: $('#firstNameAuthor').val(),
            lastName: $('#lastNameAuthor').val(),
            page: 0
        };

        this._searchAllSources(params);

        $('#searchAuthorSpinner').hide();
    },

    _searchAllSources: function(params) {
        var self = this;
        this.sources.forEach(function(source) {
            self._searchSource(source, params);
        });
    },

    _searchSource: function(source, params) {
        var self = this;

        this.authorityService.search(source, params, {
            onSuccess: function(results) {
                self.searchResults[source].render(results);
                self.pagination[source].render(results);
            },
            onError: function(error) {
                self.searchResults[source].showError();
            }
        });
    },

    _onPageChange: function(source, page) {
        var params = {
            firstName: $('#firstNameAuthor').val(),
            lastName: $('#lastNameAuthor').val(),
            page: page
        };
        this._searchSource(source, params);
    },

    _onAuthoritySelected: function(authority) {
        this._showPerson(
            authority.name,
            authority.description,
            authority.uri,
            authority.importerId,
            authority.importerName
        );
        $('#selectAuthorityModel').modal('hide');
        $('#authorModal').modal('hide');
        this._resetModal();
    },

    _showPerson: function(name, description, uri, importerId, importerName) {
        $('#name').val(name);

        if (description !== undefined && description !== ' - ' && description !== null) {
            $('#description').val(description);
        }

        if (uri !== undefined && uri !== '') {
            $('#uri').val(uri);
            $('#tr-uriAuthor').show();
        }

        if (importerId !== undefined) {
            $('#importerId').val(importerId);
        }

        if (importerName !== undefined) {
            $('#importerName').val(importerName);
            $('#importerName').show();
            $('#tr-importerName').show();
        }
    },

    _getPersonAuthority: function(uri) {
        var self = this;
        var url = CitesphereAuthority.Config.getEndpoint('getAuthority');

        $.get(url + '?uri=' + encodeURIComponent(uri), function(data) {
            $('#uriLoadingFoundAuthor').attr('data-authority-uri', data.uri);
            var content = 'Authority <b>' + uri + '</b>';

            if (data.userAuthorityEntries && data.userAuthorityEntries.length > 0) {
                content += '<br><br>This authority entry has already been imported by you:';
                content += '<ul class="foundAuthorities">';
                data.userAuthorityEntries.forEach(function(elem) {
                    content += '<li>' + elem.name + '</li>';
                });
                content += '</ul>';
            } else if (data.importedAuthority) {
                content += '<br><br>Following authority can be imported:<br>' + data.importedAuthority.name;
                content += '<button id="authorCreateAuthority" type="submit" class="btn btn-link pull-right">';
                content += '<b>Import this authority</b>';
                content += '<div style="display: none;">';
                content += '<p class="name">' + data.importedAuthority.name + '</p>';
                content += '<p class="uri">' + data.importedAuthority.uri + '</p>';
                content += '<p class="importerId">' + (data.importedAuthority.importerId || '') + '</p>';
                content += '</div></button>';
            } else {
                content += '<br><br>No authorities found for the given URI<br>';
            }

            $('#uriLoadingFoundAuthor').attr('data-content', content);
            $('#uriLoadingFoundAuthor').attr('data-authority-uri', uri);
            $('#uriLoadingFoundAuthor').show();
            $('#uriLoadingFoundAuthor').popover('show');
        })
        .fail(function() {
            $('#uriLoadingFailureAuthor').show();
        })
        .always(function() {
            $('#uriLoadingSpinnerAuthor').hide();
        });
    },

    _resetModal: function() {
        $('#firstNameAuthor').val('');
        $('#lastNameAuthor').val('');
        $('#idAuthor').attr('data-Author-id', '');
        $('#uriAuthor').val('');
        this._resetAuthorityCreation();
    },

    _resetAuthorityCreation: function() {
        $('#uriLoadingFoundAuthor').hide();
        $('#uriLoadingFailureAuthor').hide();
        $('#uriLoadingSpinnerAuthor').hide();
        $('#uriLoadingFoundAuthor').popover('hide');
        $('#uriLoadingFailureAuthor').popover('hide');
    }
};

CitesphereAuthority.EditItemHandler = function(options) {
    this.options = options || {};
    this.authorityService = new CitesphereAuthority.AuthorityService();
    this.searchResults = {};
    this.pagination = {};
    this.sources = options.sources || ['user', 'group', 'viaf', 'conceptpower'];
    this.affCount = 0;
    this._currentModalType = null;
    this._currentPersonType = null;

    this._initConnectors();
    this._initComponents();
    this._createAffiliationTemplate();
};

CitesphereAuthority.EditItemHandler.prototype = {
    _initConnectors: function() {
        var self = this;

        this.sources.forEach(function(source) {
            var ConnectorClass;
            switch (source) {
                case 'conceptpower':
                    ConnectorClass = CitesphereAuthority.ConceptpowerConnector;
                    break;
                case 'viaf':
                    ConnectorClass = CitesphereAuthority.ViafConnector;
                    break;
                case 'isiscb':
                    ConnectorClass = CitesphereAuthority.IsiscbConnector;
                    break;
                case 'user':
                    ConnectorClass = CitesphereAuthority.UserConnector;
                    break;
                case 'group':
                    ConnectorClass = CitesphereAuthority.GroupConnector;
                    break;
                default:
                    return;
            }
            self.authorityService.registerConnector(source, new ConnectorClass());
        });
    },

    _initComponents: function() {
        var self = this;

        this.sources.forEach(function(source) {
            self.searchResults[source] = new CitesphereAuthority.SearchResults({
                containerId: source + 'AuthoritySearchResult',
                source: source,
                showImporterInfo: false,
                onSelect: function(authority) {
                    self._onAuthoritySelected(authority);
                }
            });

            self.pagination[source] = new CitesphereAuthority.Pagination({
                containerId: source + 'Authority-pagination-top',
                onPageChange: function(page) {
                    self._onPageChange(source, page);
                }
            });
        });
    },

    _createAffiliationTemplate: function() {
        this.affiliationTemplate = $($.parseHTML(
            '<div class="aff-entry">' +
            '<hr>' +
            '<div class="form-group">' +
            '<div class="pull-right remove-aff"><a>Remove</a></div>' +
            '<label for="affiliation">Affiliation:</label>' +
            '<input type="text" class="form-control" id="firstName" placeholder="Affiliation">' +
            '</div>' +
            '<button class="btn btn-primary search" data-toggle="modal" data-target="#selectAuthorityModel" ' +
            'style="margin-left: 76%" disabled>Search Affiliation <i id="searchSpinner" class="fas fa-spinner fa-spin text-info" style="color: white" hidden></i>' +
            '</button>' +
            '<div class="form-group">' +
            '<label for="uri">Affiliation URI:</label>' +
            '<input type="text" class="form-control affUri" placeholder="URI"/>' +
            '</div>' +
            '</div>'
        ));
    },

    init: function() {
        var self = this;

        this._hideLoadingIndicators();

        $('#uriLoadingFoundAuthor').popover();
        $('#uriLoadingFailureAuthor').popover();
        $('#uriLoadingFoundEditor').popover();
        $('#uriLoadingFailureEditor').popover();
        $('#uriLoadingFoundCreator').popover();
        $('#uriLoadingFailureCreator').popover();

        $('#submitForm').on('click', function(e) {
            self._constructPersonArray('author', 'author', 0);
            self._constructPersonArray('editor', 'editor', 0);
            var creatorSubmitCount = 0;
            $('.creator-row').each(function(idx, elem) {
                var ele = $(elem).children().first();
                if (!(ele.attr('id') === 'editor' || ele.attr('id') === 'author')) {
                    var roleCount = self._constructPersonArray('creator', ele.attr('id'), creatorSubmitCount);
                    creatorSubmitCount = creatorSubmitCount + roleCount;
                }
            });
            self._createConceptTags();
        });

        this._initPersonEvents('Author');
        this._initPersonEvents('Editor');
        this._initPersonEvents('Creator');

        $(document).on('click', '.remove-aff', function() {
            $(this).closest('.aff-entry').remove();
        });

        $('#closeAuthoritySearchResult').on('click', function() {
            $('#selectAuthorityModel').modal('hide');
            $('#selectAuthorityModel a:first').tab('show');
        });

        this._bindExistingPersonHandlers();

        $('#addConceptButton').on('click', function(e) {
            e.preventDefault();
            self._addConcept();
        });
    },

    _initPersonEvents: function(personType) {
        var self = this;
        var personTypeLower = personType.toLowerCase();

        $('#firstName' + personType + ', #lastName' + personType).on('keyup', function() {
            self._allowSearchAndAdd(personType);
        });

        $('#add' + personType + 'Button').on('click', function(e) {
            if (personType === 'Creator') {
                var target = $(e.target);
                if (target.attr('data-creator-type') != null) {
                    self._savePersonDetails(target.attr('data-creator-type'), 'Creator');
                } else {
                    self._savePersonDetails('', 'Creator');
                }
            } else {
                self._savePersonDetails(personType, personType);
            }
        });

        $('#add' + personType + 'ModalCancel').on('click', function() {
            $('#' + personTypeLower + 'Modal').modal('hide');
            self._resetPersonCreationModal(personType);
        });

        $('#search' + personType).on('click', function() {
            self._searchAuthorities(personType, personType);
        });

        $('#add' + personType + 'Affiliation').on('click', function() {
            self._addAffiliation(personType, self.affCount + 1);
        });

        var timer = null;
        $('#uri' + personType).on('change', function() {
            self._resetPersonAuthorityCreation(personType);
            $('#uriLoadingSpinner' + personType).show();
            var uri = $(this).val();
            clearTimeout(timer);
            timer = setTimeout(function() {
                self._getPersonAuthority(uri, personType);
            }, 1000);
        });

        $('#' + personTypeLower + 'IconContainer').on('click', '.popover #' + personTypeLower + 'CreateAuthority', function() {
            var uri = $('#uriLoadingFound' + personType).attr('data-authority-uri');
            var importAuthorityURL = '/auth/authority/import?uri=' + uri;

            $.ajax({
                dataType: 'json',
                type: 'POST',
                url: importAuthorityURL,
                data: {csrfParameterName: CitesphereAuthority.Config.getCsrfToken()},
                success: function(data) {
                    $('#' + personTypeLower + 'CreateAuthority').hide();
                    $('#uri' + personType + 'LocalId').val(data.id);
                    $('#' + personTypeLower + 'AuthorityUsed').html('Created new authority entry <i>' + data.name + '</i>.');
                    $('#' + personTypeLower + 'AuthorityCreationFeedback').html('<div class="text-success" style="margin-top:10px;">Authority entry has been created!</div>');
                    self._showPersonNameInModal(data.name, personType);
                    $('#uriLoadingFound' + personType).popover('hide');
                    $('#add' + personType + 'Button').prop('disabled', false);
                }
            });
        });

        $('#' + personTypeLower + 'IconContainer').on('click', '.popover .foundAuthorities li a', function(event) {
            var authId = $(this).attr('data-authority-id');
            $('#uri' + personType + 'LocalId').val(authId);
            $('#' + personTypeLower + 'AuthorityUsed').html('Using stored authority entry <i>' + $(this).attr('data-authority-name') + '</i>.');
            self._showPersonNameInModal($(this).attr('data-authority-name'), personType);
            $('#uriLoadingFound' + personType).popover('hide');
            $('#add' + personType + 'Button').prop('disabled', false);
            event.preventDefault();
        });
    },

    _hideLoadingIndicators: function() {
        var types = ['Author', 'Editor', 'Creator'];
        types.forEach(function(type) {
            $('#uriLoadingSpinner' + type).hide();
            $('#uriLoadingFailure' + type).hide();
            $('#uriLoadingFound' + type).hide();
            $('#search' + type + 'Spinner').hide();
        });
    },

    _bindExistingPersonHandlers: function() {
        var self = this;
        var types = ['author', 'editor', 'creator'];

        types.forEach(function(type) {
            var Type = type.charAt(0).toUpperCase() + type.slice(1);

            $('.edit-' + type).on('click', function() {
                var item = $(this).parent();
                self._editPerson(Type, item[0]);
            });
            $('.edit-' + type).css('cursor', 'pointer');

            $('.remove-' + type).on('click', self._removePerson);
            $('.remove-' + type).css('cursor', 'pointer');
        });

        $('.remove-concept').on('click', this._removeConcept);
        $('.remove-concept').css('cursor', 'pointer');

        $('.creatorModalLink').on('click', function(e) {
            var target = $(e.target);
            var creatorType = target.attr('data-creator-type');
            creatorType = creatorType.charAt(0).toUpperCase() + creatorType.slice(1);
            $('#creatorLabel').text('Enter ' + creatorType + ' Information');
            $('#addCreatorButton').text('Add ' + creatorType);
            $('#addCreatorButton').attr('data-creator-type', target.attr('data-creator-type'));
        });
    },

    _allowSearchAndAdd: function(element) {
        if ($('#firstName' + element).val() === '' && $('#lastName' + element).val() === '') {
            $('#search' + element).prop('disabled', true);
            $('#add' + element + 'Button').prop('disabled', true);
        } else {
            $('#search' + element).prop('disabled', false);
            $('#add' + element + 'Button').prop('disabled', false);
        }
    },

    _searchAuthorities: function(modalType, personType) {
        var self = this;

        this._currentModalType = modalType;
        this._currentPersonType = personType;

        $('#selectAuthorityModel').on('hidden.bs.modal', function() {
            $('#selectAuthorityModel a:first').tab('show');
        });

        $('#search' + modalType + 'Spinner').show();

        this.sources.forEach(function(source) {
            self.pagination[source].destroy();
        });

        var params = {
            firstName: $('#firstName' + personType).val(),
            lastName: $('#lastName' + personType).val() || '',
            page: 0
        };

        this.sources.forEach(function(source) {
            self.searchResults[source].onSelect = function(authority) {
                self._onAuthoritySelectedForPerson(authority, modalType, personType);
            };
        });

        this._searchAllSources(params);

        $('#search' + modalType + 'Spinner').hide();
    },

    _searchAllSources: function(params) {
        var self = this;
        this.sources.forEach(function(source) {
            self._searchSource(source, params);
        });
    },

    _searchSource: function(source, params) {
        var self = this;

        this.authorityService.search(source, params, {
            onSuccess: function(results) {
                self.searchResults[source].render(results);
                self.pagination[source].render(results);
            },
            onError: function(error) {
                self.searchResults[source].showError();
            }
        });
    },

    _onPageChange: function(source, page) {
        var params = {
            firstName: $('#firstName' + this._currentPersonType).val(),
            lastName: $('#lastName' + this._currentPersonType).val() || '',
            page: page
        };
        this._searchSource(source, params);
    },

    _onAuthoritySelected: function(authority) {
        this._onAuthoritySelectedForPerson(authority, this._currentModalType, this._currentPersonType);
    },

    _onAuthoritySelectedForPerson: function(authority, modalType, personType) {
        var personTypeLower = personType.toLowerCase();

        if (modalType.includes('Affiliation')) {
            this._showAffiliationNameInModal(authority.name, authority.uri, personType);
        } else {
            this._showPersonNameInModal(authority.name, personType);
            $('#uri' + modalType).val(authority.uri);
        }

        $('#uri' + modalType).val(authority.uri);
        $('#' + personTypeLower + 'AuthorityUsed').html('Using stored authority entry <i>' + authority.name + '</i>.');
        $('#selectAuthorityModel').modal('hide');
    },

    _showAffiliationNameInModal: function(name, uri, personType) {
        $('#firstName' + personType).val(name);
        $('#uri' + personType).val(uri);
    },

    // Parses authority name formats and extracts first/last name
    // Handles: "Last, First (Alternate), Year-", "Last, First, Title, Year", "Last, First"
    _showPersonNameInModal: function(name, personType) {
        var personName = name;

        // Strip bracketed alternate names: "Dempsey, Hugh A. (Hugh Aylmer), 1929-" -> "Dempsey, Hugh A. , 1929-"
        if (name.includes('(')) {
            personName = name.substring(0, name.indexOf('('));
        }

        // Strip extra comma-separated parts (title/year): "Iqbal, Muhammad, Sir, 1877-1938" -> "Iqbal, Muhammad"
        if (personName.split(',').length > 2) {
            personName = personName.substring(0, personName.indexOf(',', personName.indexOf(',') + 1));
        }

        // Strip trailing year span: "Dempsey, Patrick, 1966-" -> "Dempsey, Patrick"
        if (personName.includes('-')) {
            personName = personName.trim();
            personName = personName.substring(0, personName.lastIndexOf(' '));
        }

        // Split into first/last name
        if (personName.indexOf(',') !== -1) {
            $('#firstName' + personType).val(personName.substring(personName.indexOf(',') + 1).trim());
            $('#lastName' + personType).val(personName.substring(0, personName.lastIndexOf(', ')));
        } else {
            $('#lastName' + personType).val(personName.substring(personName.lastIndexOf(' ') + 1).trim());
            $('#firstName' + personType).val(personName.substring(0, personName.lastIndexOf(' ')));
        }
    },

    _addAffiliation: function(modalType, counter) {
        var self = this;
        var modalTypeLower = modalType.toLowerCase();
        var affiliationCopy = this.affiliationTemplate.clone();

        affiliationCopy.attr('id', modalTypeLower + 'AffiliationTemplate' + counter);
        affiliationCopy.addClass('aff-info');

        affiliationCopy.find('label[for="affiliation"]').attr('for', 'firstName' + modalType + 'Affiliation' + counter);
        affiliationCopy.find('label[for="uri"]').attr('for', 'uri' + modalType + 'Affiliation' + counter);

        var affInput = affiliationCopy.find('#firstName');
        affInput.addClass('firstName' + modalType + 'Affiliation');
        affInput.attr('id', 'firstName' + modalType + 'Affiliation' + counter);
        affInput.attr('data-client-id', affInput.attr('id'));

        var searchButton = affiliationCopy.find('button');
        searchButton.attr('id', 'search' + modalType + 'Affiliation' + counter);
        searchButton.attr('data-client-id', searchButton.attr('id'));
        searchButton.find('#searchSpinner').attr('id', 'search' + modalType + 'Affiliation' + counter + 'Spinner');
        searchButton.prop('disabled', true);

        var affUri = affiliationCopy.find('.affUri');
        affUri.attr('id', 'uri' + modalType + 'Affiliation' + counter);
        affUri.attr('data-client-id', affUri.attr('id'));

        $('#' + modalTypeLower + 'Affiliations').append(affiliationCopy);

        var affiliationCounter = modalType + 'Affiliation' + counter;
        $('#firstName' + affiliationCounter).on('keyup', function() {
            self._allowSearchAndAdd(affiliationCounter);
        });

        $('#search' + affiliationCounter).on('click', function() {
            self._searchAuthorities(affiliationCounter, affiliationCounter);
        });

        this.affCount++;
    },

    _editPerson: function(modalName, item) {
        var self = this;
        var personItem = $(item);
        var modalNameLower = modalName.toLowerCase();

        $('#firstName' + modalName).val(personItem.attr('data-' + modalNameLower + '-firstname'));
        $('#lastName' + modalName).val(personItem.attr('data-' + modalNameLower + '-lastname'));
        $('#uri' + modalName).val(personItem.attr('data-' + modalNameLower + '-uri'));
        $('#id' + modalName).attr('data-' + modalNameLower + '-id', personItem.attr('id'));

        var counter = 0;
        personItem.children('.affiliation-class').each(function(idx, elem) {
            var affiliationCopy = self.affiliationTemplate.clone();
            affiliationCopy.attr('id', modalNameLower + 'AffiliationTemplate' + counter);
            affiliationCopy.addClass('aff-info');

            affiliationCopy.find('label[for="affiliation"]').attr('for', 'firstName' + modalName + 'Affiliation' + counter);
            affiliationCopy.find('label[for="uri"]').attr('for', 'uri' + modalName + 'Affiliation' + counter);

            var affInput = affiliationCopy.find('#firstName');
            affInput.addClass('firstName' + modalName + 'Affiliation');
            affInput.attr('id', 'firstName' + modalName + 'Affiliation' + counter);
            affInput.attr('data-client-id', affInput.attr('id'));
            affInput.attr('data-affiliation-name', $(elem).data('affiliationName'));
            affInput.attr('data-affiliation-id', $(elem).data('affiliationId'));
            affInput.val($(elem).data('affiliationName'));

            var searchButton = affiliationCopy.find('button');
            searchButton.attr('id', 'search' + modalName + 'Affiliation' + counter);
            searchButton.attr('data-client-id', searchButton.attr('id'));
            searchButton.find('#searchSpinner').attr('id', 'search' + modalName + 'Affiliation' + counter + 'Spinner');
            searchButton.prop('disabled', false);

            var affUri = affiliationCopy.find('.affUri');
            affUri.attr('id', 'uri' + modalName + 'Affiliation' + counter);
            affUri.attr('data-client-id', affUri.attr('id'));
            affUri.attr('data-affiliation-uri', $(elem).data('affiliationUri'));
            affUri.val($(elem).data('affiliationUri'));

            $('#' + modalNameLower + 'Affiliations').append(affiliationCopy);

            var affiliationCounter = modalName + 'Affiliation' + counter;
            $('#firstName' + affiliationCounter).on('keyup', function() {
                self._allowSearchAndAdd(affiliationCounter);
            });

            $('#search' + affiliationCounter).on('click', function() {
                self._searchAuthorities(affiliationCounter, affiliationCounter);
            });

            counter++;
        });

        this.affCount = counter;
        $('#addCreatorButton').attr('data-' + modalNameLower + '-type', personItem.attr('data-' + modalNameLower + 'type'));
        $('#add' + modalName + 'Button').text('Update ' + modalName);
        $('#add' + modalName + 'Button').prop('disabled', false);

        $('#' + modalNameLower + 'Modal').modal('show');
        if ($('#firstName' + modalName).val() !== '' || $('#lastName' + modalName).val() !== '') {
            $('#search' + modalName).prop('disabled', false);
        }
    },

    _savePersonDetails: function(personType, modalName) {
        var self = this;
        var modalNameLower = modalName.toLowerCase();
        var personSpan;
        var personTypeLower = personType.toLowerCase();

        if ($('#id' + modalName).attr('data-' + modalNameLower + '-id') != null &&
            $('#id' + modalName).attr('data-' + modalNameLower + '-id').length > 0) {
            personSpan = $('#' + $('#id' + modalName).attr('data-' + modalNameLower + '-id'));
            personTypeLower = personSpan.attr('data-creator-type').toLowerCase();
        } else {
            var id = personTypeLower + $('.' + personTypeLower + '-item').length;
            personSpan = $('<span id=' + id + '>');
        }

        personSpan.attr('class', 'label label-warning ' + personTypeLower + '-item');
        personSpan.html('');

        var firstname = $('#firstName' + modalName).val();
        var lastname = $('#lastName' + modalName).val();
        var uri = $('#uri' + modalName).val();
        var localAuthority = $('#uri' + modalName + 'LocalId').val();

        personSpan.attr('data-' + modalNameLower + '-firstname', firstname);
        personSpan.attr('data-' + modalNameLower + '-lastname', lastname);
        personSpan.attr('data-' + modalNameLower + '-uri', uri);
        personSpan.attr('data-' + modalNameLower + '-authority-id', localAuthority);
        personSpan.attr('data-creator-type', personTypeLower);

        var affiliationsList = [];
        $('#' + modalNameLower + 'Affiliations').children().each(function(idx, elem) {
            var affName = $(elem).find('.firstName' + modalName + 'Affiliation');
            var affUri = $(elem).find('.affUri');
            if (affName.val().length !== 0) {
                var affSpan = $('<span>');
                affSpan.addClass('affiliation-class');
                affSpan.attr('data-affiliation-name', affName.val());
                affSpan.attr('data-affiliation-uri', affUri.val());
                affiliationsList.push(affName.val());
                personSpan.append(affSpan);
            }
        });

        var affiliationString = '';
        if (affiliationsList.length !== 0) {
            affiliationString = ' (' + $.grep(affiliationsList, Boolean).join(', ') + ')';
        }

        personSpan.append(lastname + ', ' + firstname + affiliationString + '&nbsp;&nbsp; ');

        var editIcon = $('<i class="icon-edit edit-' + modalNameLower + '" style="color: white; font-size: 12px;"></i>');
        var deleteIcon = $('<i class="icon-circle-close remove-' + modalNameLower + '" style="color: white; font-size: 12px;"></i>');

        editIcon.on('click', function() {
            var personItem = $(this).parent();
            self._editPerson(modalName, personItem[0]);
        });
        deleteIcon.on('click', this._removePerson);

        personSpan.append(editIcon);
        personSpan.append(deleteIcon);
        $('#' + personTypeLower + 'List').append(personSpan);
        $('#' + personTypeLower + 'List').append('&nbsp;&nbsp; ');
        $('#' + modalNameLower + 'Modal').modal('hide');
        this._resetPersonCreationModal(modalName);
    },

    // Builds hidden form fields for person data on form submission
    _constructPersonArray: function(arrayName, role, iter) {
        var self = this;
        var creator, otherCreatorCount = 0;
        var roleLower = role.toLowerCase();

        if (arrayName === 'creator') {
            creator = 'otherCreator';
        } else {
            creator = arrayName;
        }

        $('.' + roleLower + '-item').each(function(idx, person) {
            var creatorSubmitCount = idx + iter;

            var fields = [
                {suffix: '.id', attr: 'data-' + arrayName + '-id'},
                {suffix: '.firstName', attr: 'data-' + arrayName + '-firstname'},
                {suffix: '.lastName', attr: 'data-' + arrayName + '-lastname'},
                {suffix: '.role', value: role},
                {suffix: '.uri', attr: 'data-' + arrayName + '-uri'},
                {suffix: '.localAuthorityId', attr: 'data-' + arrayName + '-authority-id'}
            ];

            fields.forEach(function(field) {
                var input = $('<input>');
                input.attr('type', 'hidden');
                input.attr('id', creator + 's' + creatorSubmitCount + field.suffix);
                input.attr('name', creator + 's[' + creatorSubmitCount + ']' + field.suffix);
                input.attr('value', field.value !== undefined ? field.value : $(person).attr(field.attr));
                $('#editForm').append(input);
            });

            $(person).children('.affiliation-class').each(function(idx2, affiliation) {
                var affFields = [
                    {suffix: '.name', attr: 'data-affiliation-name'},
                    {suffix: '.id', attr: 'data-affiliation-id'},
                    {suffix: '.uri', attr: 'data-affiliation-uri'}
                ];

                affFields.forEach(function(field) {
                    var input = $('<input>');
                    input.attr('type', 'hidden');
                    input.attr('id', creator + 's' + creatorSubmitCount + '.affiliations' + idx2 + field.suffix);
                    input.attr('name', creator + 's[' + creatorSubmitCount + '].affiliations[' + idx2 + ']' + field.suffix);
                    input.attr('value', $(affiliation).attr(field.attr));
                    $('#editForm').append(input);
                });
            });

            otherCreatorCount += 1;
        });

        return otherCreatorCount;
    },

    // Builds hidden form fields for concept tags on form submission
    _createConceptTags: function() {
        $('#conceptTags').children('span').each(function(idx, tag) {
            var fields = [
                {suffix: '.conceptId', attr: 'data-concept-id'},
                {suffix: '.conceptTypeId', attr: 'data-concept-type-id'},
                {suffix: '.conceptUri', attr: 'data-concept-uri'},
                {suffix: '.conceptName', attr: 'data-concept-name'},
                {suffix: '.conceptTypeName', attr: 'data-type-name'},
                {suffix: '.conceptTypeUri', attr: 'data-type-uri'}
            ];

            fields.forEach(function(field) {
                var input = $('<input>');
                input.attr('type', 'hidden');
                input.attr('id', 'conceptTags' + idx + field.suffix);
                input.attr('name', 'conceptTags[' + idx + ']' + field.suffix);
                input.attr('value', $(tag).attr(field.attr));
                $('#editForm').append(input);
            });
        });
    },

    _addConcept: function() {
        var conceptId = $('#addConceptConceptSelect');
        var conceptType = $('#addConceptTypeSelect');

        var conceptSpan = $('<span class="badge"></span>');
        conceptSpan.attr('data-concept-uri', conceptId.val());
        conceptSpan.attr('data-type-uri', conceptType.val());

        var text = $('#addConceptConceptSelect option:selected').text();
        var typeName = $('#addConceptTypeSelect option:selected').text();
        conceptSpan.text(text + ' | ' + typeName + ' ');

        var deleteIcon = $('<i class="icon-circle-close remove-concept" style="cursor: pointer; color: white; font-size: 12px;"></i>');
        deleteIcon.on('click', this._removeConcept);
        conceptSpan.append(deleteIcon);
        $('#conceptTags').append(conceptSpan);

        $('#addConceptModal').modal('hide');
    },

    _getPersonAuthority: function(uri, personType) {
        var self = this;
        var personTypeLower = personType.toLowerCase();
        var zoteroGroupId = CitesphereAuthority.Config.getZoteroGroupId();

        $.get('/auth/authority/get?uri=' + encodeURIComponent(uri) + '&zoteroGroupId=' + zoteroGroupId, function(data) {
            $('#uriLoadingFound' + personType).attr('data-authority-uri', data.uri);
            var content = 'Authority <b>' + uri + '</b>';

            if (data.userAuthorityEntries && data.userAuthorityEntries.length > 0) {
                content += '<br><br>This authority entry has already been imported by you:';
                content += '<ul class="foundAuthorities">';
                data.userAuthorityEntries.forEach(function(elem) {
                    content += '<li>' + elem.name;
                    content += ' [<a href="" data-authority-id="' + elem.id + '" data-authority-name="' + elem.name + '">Use this one</a>]';
                    content += '</li>';
                });
                content += '</ul>';
            }

            if (data.datasetAuthorityEntries && data.datasetAuthorityEntries.length > 0) {
                content += '<br><br>This authority entry has already been imported by someone else for this dataset:';
                content += '<ul class="foundAuthorities">';
                data.datasetAuthorityEntries.forEach(function(elem) {
                    content += '<li>' + elem.name;
                    content += ' [<a href="" data-authority-id="' + elem.id + '" data-authority-name="' + elem.name + '">Use this one</a>]';
                    content += '</li>';
                });
                content += '</ul>';
            }

            if ((!data.userAuthorityEntries || data.userAuthorityEntries.length === 0) &&
                (!data.datasetAuthorityEntries || data.datasetAuthorityEntries.length === 0)) {
                content += '<br><br>No authorities found for the given URI<br>';
            }

            $('#uriLoadingFound' + personType).attr('data-content', content);
            $('#uriLoadingFound' + personType).attr('data-authority-uri', uri);
            $('#uriLoadingFound' + personType).show();
            $('#uriLoadingFound' + personType).popover('show');
        })
        .fail(function() {
            $('#uriLoadingFailure' + personType).show();
        })
        .always(function() {
            $('#uriLoadingSpinner' + personType).hide();
        });
    },

    _resetPersonCreationModal: function(modalType) {
        var modalNameLower = modalType.toLowerCase();
        $('#firstName' + modalType).val('');
        $('#lastName' + modalType).val('');
        $('#id' + modalType).attr('data-' + modalType + '-id', '');
        $('#' + modalNameLower + 'Affiliations').children().remove();
        $('#uri' + modalType).val('');

        if (modalType === 'Creator') {
            $('#addCreatorButton').attr('data-creator-type', '');
            $('#creatorLabel').text('Enter Creator Information');
            $('#addCreatorButton').text('Add Creator');
        }

        $('#add' + modalType + 'Button').text('Add ' + modalType);
        $('#add' + modalType + 'Button').prop('disabled', true);
        this._resetPersonAuthorityCreation(modalType);
        this.affCount = 0;
    },

    _resetPersonAuthorityCreation: function(personType) {
        $('#uriLoadingFound' + personType).hide();
        $('#uriLoadingFailure' + personType).hide();
        $('#uriLoadingSpinner' + personType).hide();
        $('#uriLoadingFound' + personType).popover('hide');
        $('#uriLoadingFailure' + personType).popover('hide');
        $('#' + personType.toLowerCase() + 'AuthorityUsed').html('');
    },

    _removePerson: function(e) {
        var deleteIcon = e.currentTarget;
        var person = $(deleteIcon).parent();
        person.remove();
    },

    _removeConcept: function(e) {
        var deleteIcon = e.currentTarget;
        var concept = $(deleteIcon).parent();
        concept.remove();
    },

    loadFields: function() {
        var self = this;
        var itemType = $('#items option:selected').val();

        $('#displayMessage').html("<i class='glyphicon glyphicon-refresh spinning'></i> Loading form fields");
        $('#messageModal').modal('show');

        $.ajax({
            url: '/citesphere/auth/items/' + itemType + '/fields',
            type: 'GET',
            success: function(changedFields) {
                $('form input').each(function(idx, elem) {
                    $(elem).parent().closest('tr').hide();
                });
                for (var i = 0; i < changedFields.length; i++) {
                    var fieldId = changedFields[i];
                    if (fieldId === 'date') {
                        fieldId = 'dateFreetext';
                    }
                    $('form input#' + fieldId).parent().closest('tr').show();
                }
                $('#messageModal').modal('hide');
            },
            error: function() {
                $('#displayMessage').html("<i class='glyphicon glyphicon-remove-sign'></i> Error loading the form fields. Try again later.");
                $('#messageModal').modal('show');
                setTimeout(function() {
                    $('#messageModal').modal('hide');
                }, 3000);
            }
        });

        $.ajax({
            url: '/citesphere/auth/items/' + itemType + '/creators',
            type: 'GET',
            success: function(creators) {
                $('.creator-row').each(function(idx, elem) {
                    $(elem).hide();
                });
                for (var i = 0; i < creators.length; i++) {
                    if ($('[id=' + creators[i]).length > 0) {
                        $('[id=' + creators[i]).parent().closest('tr').addClass('creator-row');
                        $('[id=' + creators[i]).parent().closest('tr').show();
                    } else if (creators[i] !== 'editor' && creators[i] !== 'author') {
                        self._createCreatorRow(creators[i]);
                    }
                }
            },
            error: function() {
                $('#displayMessage').html("<i class='glyphicon glyphicon-remove-sign'></i> Error loading the creators. Try again later.");
                $('#messageModal').modal('show');
                setTimeout(function() {
                    $('#messageModal').modal('hide');
                }, 3000);
            }
        });
    },

    _createCreatorRow: function(creatorType) {
        var self = this;

        var creatorRow = $('<tr>');
        creatorRow.css('display', 'table-row');
        creatorRow.addClass('creator-row');

        var creatorLabel = $('<td>');
        creatorLabel.addClass('creator');
        creatorLabel.css('text-transform', 'capitalize');
        creatorLabel.attr('id', creatorType);
        creatorLabel.append(creatorType);
        creatorRow.append(creatorLabel);

        var creatorData = $('<td>');
        var creatorList = $('<span>');
        creatorList.attr('id', creatorType + 'List');
        creatorList.css('font-size', '18px');
        creatorData.append(creatorList);

        var addIconDiv = $('<div>');
        addIconDiv.addClass('pull-right');

        var iconLink = $('<a>');
        iconLink.attr('data-toggle', 'modal');
        iconLink.attr('data-creator-type', creatorType);
        iconLink.attr('data-target', '#creatorModal');

        var iconImg = $('<i>');
        iconImg.addClass('icon-circle-add');
        iconLink.append(iconImg);
        iconLink.append('Add ' + creatorType);
        addIconDiv.append(iconLink);
        creatorData.append(addIconDiv);
        creatorRow.append(creatorData);
        creatorRow.insertAfter($('.creator').last().parent());

        $('#creatorLabel').css('text-transform', 'capitalize');
        $('#creatorLabel').text('Enter ' + creatorType + ' Information');
        $('#addCreatorButton').css('text-transform', 'capitalize');
        $('#addCreatorButton').text('Add ' + creatorType);
        $('#addCreatorButton').attr('data-creator-type', creatorType);

        iconLink.on('click', function(e) {
            self._creatorLinkHandler($(e.target));
        });
    },

    _creatorLinkHandler: function(target) {
        var creatorType = target.attr('data-creator-type');
        creatorType = creatorType.charAt(0).toUpperCase() + creatorType.slice(1);
        $('#creatorLabel').text('Enter ' + creatorType + ' Information');
        $('#addCreatorButton').text('Add ' + creatorType);
        $('#addCreatorButton').attr('data-creator-type', target.attr('data-creator-type'));
    }
};
