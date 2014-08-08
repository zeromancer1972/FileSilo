// core
var confirmDelete = function(id) {
	$("#confirmdelete").attr("rel", id).modal( {
		backdrop : "static"
	});
}
var actionDelete = function() {
	dojo.byId($("#confirmdelete").attr("rel")).click();
}

var confirmRemoveLogs = function() {
	$("#confirmdremovelogs").modal( {
		backdrop : "static"
	});
}

var actionRemoveLogs = function() {
	$(".btnRemoveLogs").click();
}

var dialogURL = function(urlCollection, urlKey) {
	$("#urlCollection").val(urlCollection);
	$("#urlKey").val(urlKey);
	$("#dialogurl").modal( {
		backdrop : "static"
	});
}

var doLogoutDialog = function() {
	$("#confirmlogout").modal( {
		backdrop : "static"
	});
}

var doLogout = function() {
	require( [ "dojo/cookie" ], function(cookie) {
		if (cookie("DomAuthSessId")) {
			cookie("DomAuthSessId", null, {
				path : "/",
				expires : "Thu, 01 Jan 1970 00:00:00 GMT"
			});
		}
		if (cookie("LtpaToken")) {
			cookie("LtpaToken", null, {
				path : "/",
				expires : "Thu, 01 Jan 1970 00:00:00 GMT"
			});
		}
		if (cookie("LtpaToken2")) {
			cookie("LtpaToken2", null, {
				path : "/",
				expires : "Thu, 01 Jan 1970 00:00:00 GMT"
			});
		}

		location.reload();
	})
}

require( [ "dojo/_base/lang", "ibm/xsp/widget/layout/DateTextBox",
		"ibm/xsp/widget/layout/TimeTextBox",
		"ibm/xsp/widget/layout/DateTimeTextBox" ], function(lang, DateTextBox,
		TimeTextBox, DateTimeTextBox) {
	var a = {};
	lang.mixin(a, {
		postCreate : function() {
			this.inherited(arguments);
		}
	});
	DateTextBox.extend(a);
	TimeTextBox.extend(a);
	DateTimeTextBox.extend(a);
});

dojo.addOnLoad( function() {
	// file upload
	$('.bsfile').bootstrapFileInput();
	dojo.query(".customerKey a").forEach( function(node, index, nodelist) {
		dojo.connect(node, "click", function(evt) {
			showKey(evt.target.innerHTML)
		})
	})
	
	// file download trash (thanks to @flinden68)
	$(".table a[role='button']").addClass("btn btn-danger");
	$(".table a[role='button']").html("<icon class='glyphicon glyphicon-trash'></icon>");
	
	$('.disabled').focus( function() {
		$(this).blur();
	});
	$('.nav-disabled').click( function(e) {
		e.preventDefault();
	});
	$(".datepicker").datepicker( {
		startDate : new Date(),
		todayBtn : true,
		calendarWeeks : true,
		autoclose : true,
		todayHighlight : true
	});
	$(".select-all").focus( function(e) {
		$(this).select();
	}).mouseup( function() {
		return false;
	});
	$("[data-toggle=tooltip]").tooltip( {

	});

})

var showKey = function(id) {
	var box = dojo.query(".js-dlgID")[0];
	box.value = keyURL + "?key=" + id
	dijit.byId("dlgCopyID").show()
}

$(document).on(
		'click',
		'.panel-heading span.clickable',
		function(e) {
			var $this = $(this);
			if (!$this.hasClass('panel-collapsed')) {
				$this.parents('.panel').find('.panel-body').fadeOut(
						{
							complete : function() {
								$this.find('i').removeClass(
										'glyphicon-chevron-up').addClass(
										'glyphicon-chevron-down');
							}
						});
				$this.addClass('panel-collapsed');

			} else {
				$this.parents('.panel').find('.panel-body').fadeIn(
						{
							complete : function() {
								$this.find('i').removeClass(
										'glyphicon-chevron-down').addClass(
										'glyphicon-chevron-up');
							}
						});
				$this.removeClass('panel-collapsed');

			}
		});

dojo._xhr = dojo.xhr;
var loadOld;
function hijacked(response, ioArgs) {
	// your code or function here (pre-processed)
	loadOld(response, ioArgs);
	// your code or function here (post-processed)
	$("[data-toggle=tooltip]").tooltip( {

	});
}
dojo.xhr = function(mode, args, bool) {
	loadOld = args["load"];
	args["load"] = hijacked;
	dojo._xhr(mode, args, bool);
}