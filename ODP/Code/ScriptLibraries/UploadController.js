/**
 * Class UploadController Contains massively restructured code based upon
 * example from Julian Buss:
 * http://julianbuss.net/web/youatnotes/blog-jb.nsf/dx/html5-multi-file-upload-with-xpages-my-solution-in-a-nutshell.htm
 * 
 * @author: gernot.hummer
 * 
 * @version 1.0
 */
function UploadController() {

	var _self = this;
	_self.TAG = "[UPLOADCONTROLLER]";
	_self.DEFAULT_LIMIT = 1048576;
	_self.MB_THRESHOLD = 1048576;
	_self.DEFAULT_ERROR = "Die Datei überschreitet das Größenlimit.";

	_self.source = null;
	_self.response = null;
	_self.containerID = "";
	_self.url = "";
	_self.uploadID = "";
	_self.form = null;
	_self.inputHTML = "<input id=\"multiUploadInput_@@@\" type=\"file\" multiple=\"true\" label=\"Select Some Files\""
			+ " uploadOnSelect=\"false\" name=\"uploadedfile\" class=\"\" />";
	_self.random = "";

	/**
	 * function init()
	 * 
	 * @param url -
	 *            String. The URL to post ajax upload calls to.
	 * @param uploadID -
	 *            String. The id of the original single file upload element
	 * @param multiUploadID -
	 *            String. The id of the container to place the multi upload
	 *            element in.
	 * @param responseID -
	 *            String. The id of the container to render the file queue into
	 * @param buttonSelectId -
	 *            String. The id of the button that selects files
	 * @param buttonResetId -
	 *            String. The id of the button that resets the file queue
	 * @param buttonUploadId -
	 *            String. The id of the button that posts files to the server
	 *            via ajax calls.
	 */
	_self.init = function(url, uploadID, multiUploadID, responseID, buttonSelectId, buttonResetId, buttonUploadId, container, random) {
		_self.inputHTML = _self.inputHTML.replace("@@@", random);
		_self.random = random;
		_self.url = url;
		_self.uploadID = uploadID;
		_self.buttonUploadId = buttonUploadId;
		_self.buttonResetId = buttonResetId;
		_self.container = container;
		_self.response = dojo.byId(responseID);
		_self.connectUpload(multiUploadID);
		dojo.connect(dojo.byId(buttonSelectId), "onclick", dojo.partial(_self.showDialog));
		dojo.connect(dojo.byId(buttonResetId), "onclick", dojo.partial(_self.reset, multiUploadID));
		dojo.connect(dojo.byId(buttonUploadId), "onclick", dojo.partial(_self.doUpload, url, uploadID))

		if (_self.source == null || _self.response == null) {
			console.log(_self.TAG + ": Necessary document nodes were not found.");
		}

	}

	/**
	 * function connectUpload() Generates a multi upload input element and
	 * connects it via dojo event handling
	 * 
	 * @param multiUploadID -
	 *            String. The id of the container to place the multi upload
	 *            element in
	 */
	_self.connectUpload = function(multiUploadID) {
		_self.containerID = multiUploadID;
		document.getElementById(multiUploadID).innerHTML = _self.inputHTML;
		_self.response.innerHTML = "";
		_self.source = dojo.byId("multiUploadInput_" + _self.random);
		_self.form = XSP.findForm("multiUploadInput_" + _self.random);

		// connect onchange event of upload element with function showQueue()
		dojo.connect(_self.source, "onchange", _self.showQueue);

	}

	/**
	 * function showDialog() Provokes the file picker of the browser to select
	 * multiple files.
	 */
	_self.showDialog = function(buttonUploadId, buttonResetId) {
		dojo.byId(_self.buttonUploadId).style.display = "";
		dojo.byId(_self.buttonResetId).style.display = "";
		_self.source.click();
	}

	/**
	 * function showQueue() Renders the current file queue into the target
	 * result container. This method is connected to the onchange event of the
	 * multi upload input element.
	 */
	_self.showQueue = function() {
		try {
			var files = document.getElementById(dojo.attr(_self.source, "id")).files;
			var responseHTML = '';
			if (files && files.length > 0) {
				responseHTML = '<table class="table table-striped" style="width:100%;">';
				responseHTML += '<thead style="color:#545454;"><tr><th style="font-weight:bold;width:46px">Size</th><th style="font-weight:bold">Files to Upload</th></tr></thead><tbody  style="color:#a0a0a0">';
				for ( var i = 0; i < files.length; i++) {
					_self.file = files[i];
					var size = 0;
					if (_self.file.size > _self.MB_THRESHOLD)
						size = (Math.round(_self.file.size / (_self.MB_THRESHOLD))).toString() + ' MB';
					else
						size = (Math.round(_self.file.size / 1024)).toString() + ' KB';
					responseHTML += '<tr><td>' + size + '</td><td>' + _self.file.name + '</td></tr>'
				}
				responseHTML += '</tbody></table>'
			}
			_self.response.innerHTML = responseHTML;
		} catch (e) {
			console.log(_self.TAG + ": " + e);
		}
	}

	/**
	 * function reset() Convenience method to re-initialize the upload element
	 */
	_self.reset = function() {
		_self.connectUpload(_self.containerID);
		XSP.partialRefreshGet(_self.container);
	}

	/**
	 * function doUpload() Handles the whole upload process of files
	 * 
	 * @param url-
	 *            String. The URL to direct POST calls to
	 * @param uploadID -
	 *            String. The id of the single file upload element.
	 */
	_self.doUpload = function(url, uploadID) {
		try {
			var files = document.getElementById(dojo.attr(_self.source, "id")).files;
			var total = files.length;

			var callback = new UploadCallback(_self, files, total);
			if (callback.hasMore()) {
				callback.call();
			}

		} catch (e) {
			console.log(_self.TAG + ": " + e);
		}
	}

	/**
	 * function doSingleUpload() Uploads a single file to the server
	 * 
	 * @param url -
	 *            String. The URL to direct POST calls to
	 * @param uploadID -
	 *            String. The id of the single file upload element
	 * @param callback -
	 *            UploadCallback. The callback interaction object for
	 *            consecutive files.
	 */
	_self.doSingleUpload = function(url, uploadID, callback) {
		try {
			if (_self._checkFileSize()) {
				_self._prepareForm(uploadID, callback);
				_self._doAjax(url, callback);

				return true;
			}

		} catch (e) {
			console.log(_self.TAG + ": " + e);
		}

		return false;

	}

	/**
	 * function setMaxSize() Set the maximum size that is allowed for uploads
	 * 
	 * @param maxSize -
	 *            number. The amount of MBs allowed.
	 */
	_self.setMaxSize = function(maxSize) {
		_self.maxSize = maxSize;
	}

	/**
	 * function _checkFileSize()
	 * 
	 * @return true if the file size is not too big.
	 */
	_self._checkFileSize = function() {
		if (!_self.maxSize)
			_self.maxSize = _self.DEFAULT_LIMIT;

		if ((_self.file.size / _self.DEFAULT_LIMIT) > _self.maxSize) {
			alert(_self.DEFAULT_ERROR);
			return false;
		}

		return true;
	}

	/**
	 * function _prepareForm() Prepares <code>FormData</code> to be dispatched
	 * to the server.
	 * 
	 * @param uploadID-
	 *            String. The id of the single file upload input element.
	 * @param callback -
	 *            UploadCallback. The callback object this controller interacts
	 *            with
	 */
	_self._prepareForm = function(uploadID, callback) {
		var formData = new FormData();
		formData.append(uploadID, callback.getCurrentFile());
		formData.append("$$viewid", dojo.query("input[name='$$viewid']")[0].value);
		formData.append("$$xspsubmitid", dojo.query("input[name='$$xspsubmitid']")[0].value);
		formData.append("$$xspsubmitvalue", dojo.query("input[name='$$xspsubmitvalue']")[0].value);
		formData.append("$$xspsubmitscroll", dojo.query("input[name='$$xspsubmitscroll']")[0].value);
		formData.append(_self.form.id, _self.form.id);
		_self.formData = formData;
	}

	/**
	 * function _doAjax() The actual ajax call logic to post a file to the
	 * server.
	 * 
	 * @param url -
	 *            String. The url to direct the POST request to.
	 * @param callback -
	 *            UploadCallback. The callback object this controller interacts
	 *            with.
	 */
	_self._doAjax = function(url, callback) {

		var xhr = new XMLHttpRequest();

		xhr.addEventListener("load", function() {
			callback.iterate();
			if (callback.hasMore()) {
				callback.call();
			} else {
				callback.finish();
			}
		}, false)

		xhr.open("POST", url);
		xhr.send(_self.formData);

	}

	return _self;
}

/**
 * Class UploadCallback
 * 
 * @param controller -
 *            The <code>UploadController</code> instance this callback
 *            interacts with
 * @param files -
 *            The list of <code>File</code>s this callback should handle
 * 
 * @author: gernot.hummer
 * @version 1.0
 */
function UploadCallback(controller, files) {

	var _self = this;
	_self.controller = controller;
	_self.files = files;
	_self.total = files.length;
	_self.current = 0;

	/**
	 * function hasMore()
	 * 
	 * @returns true the end of the file list hasn't been reached yet.
	 */
	_self.hasMore = function() {
		return _self.current < _self.total;
	}

	/**
	 * function call(); This is the main call function of this callback.
	 */
	_self.call = function() {
		_self.file = files[_self.current];
		_self.controller.doSingleUpload(_self.controller.url, _self.controller.uploadID, _self);
	}

	/**
	 * function getCurrentFile() Return the file at the current file number
	 * 
	 * @return File
	 */
	_self.getCurrentFile = function() {
		return _self.file;
	}

	/**
	 * function finish() Call this after the last ajax call has been effected.
	 */
	_self.finish = function() {
		_self.controller.reset();

	}

	/**
	 * function iterate() Move on to next file number
	 */
	_self.iterate = function() {
		_self.current++;
	}

	return _self;

}