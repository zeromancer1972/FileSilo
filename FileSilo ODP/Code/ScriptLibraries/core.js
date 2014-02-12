// core

/*
var username = dojo.byId("#{id:username}");
var password = dojo.byId("#{id:password}");
var msg = dojo.byId("#{id:loginMessage}");

dojo.connect(username, "onkeypress", function(evt) {
	if (evt.keyCode == dojo.keys.ENTER) {
		doLogin()
	}
})
dojo.connect(password, "onkeypress", function(evt) {
	if (evt.keyCode == dojo.keys.ENTER) {
		doLogin()
	}
})
dojo.connect(dojo.byId("#{id:btnLogin}"), "onclick", function(evt) {
	doLogin()
})
dojo.connect(dojo.byId("#{id:btnLogoutOk}"), "onclick", function(evt) {
	dijit.byId("#{id:dlgLogout}").hide();
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
})

dojo.connect(dojo.byId("#{id:btnLoginCancel}"), "onclick", function(evt) {
	dijit.byId("#{id:dlgLogin}").hide();
})

dojo.connect(dojo.byId("#{id:btnLogoutCancel}"), "onclick", function(evt) {
	dijit.byId("#{id:dlgLogout}").hide();
})


doLogin = function() {
	require(
			[ "dojo/request/xhr" ],
			function(xhr) {
				xhr(
						"/names.nsf?login",
						{
							method : "POST",
							data : {
								"username" : username.value,
								"password" : password.value,
								"redirectto" : "#{javascript:facesContext.getExternalContext().getRequest().getContextPath()}/$icon"
							},
							sync : true,
							handleAs : "text"
						})
						.then(
								function() {
									if (document.cookie
											.match(/DomAuthSessId|LtpaToken|LtpaToken2/)) {
										msg.innerHTML = "#{javascript:(context.getLocaleString().equals('de_DE'))?'Anmeldung erfolgreich! Weiterleitung erfolgt...':'Login successful! Redirecting...'}";
										msg.className = "lotusMessage lotusConfirm";
										location.reload();
									} else {
										msg.innerHTML = "#{javascript:(context.getLocaleString().equals('de_DE'))?'Anmeldung fehlgeschlagen! Bitte versuchen Sie es erneut.':'Login failed. Please try again.'}";
										msg.className = "lotusMessage lotusWarning";
										username.value = "";
										password.value = "";
										username.focus()
									}
								}, function(err) {
								}, function(evt) {
								});
			});
}


checkCookie = function() {
	var tokenOk = false;
	require( [ "dojo/cookie" ], function(cookie) {
		if (cookie("DomAuthSessId")) {
			tokenOk = (cookie("DomAuthSessId").length == 32);
		}
		if (cookie("LtpaToken")) {
			tokenOk = (cookie("LtpaToken").length == 32);
		}
		if (cookie("LtpaToken2")) {
			tokenOk = (cookie("LtpaToken2").length == 32);
		}
	})
	return tokenOk;
}

dialogLogin = function() {
	if (checkCookie()) {
		// dijit.byId("#{id:dlgLogout}").show();

	} else {
		username.value = "";
		password.value = "";
		msg.innerHTML = "";
		msg.className = "";
		dijit.byId("#{id:dlgLogin}").show();
	}

}

*/

doLogout = function() {
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
	dojo.query(".customerKey a").forEach( function(node, index, nodelist) {
		dojo.connect(node, "click", function(evt) {
			showKey(evt.target.innerHTML)
		})
	})
})

var showKey = function(id) {
	var box = dojo.query(".js-dlgID")[0];
	box.value = keyURL + "?key=" + id
	dijit.byId("dlgCopyID").show()
}

$(document).ready( function() {
	$(".datepicker").datepicker( {

	});
})