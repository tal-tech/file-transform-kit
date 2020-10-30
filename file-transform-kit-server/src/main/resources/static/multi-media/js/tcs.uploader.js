/**
 * 好未来云存储上传接入
 * @param window
 */
(function(window){

	function Q(_s) {
		this.qs = _s;

		this.val = function(paramName) {
			var p = [ "[\?\&]", paramName, "=([^\&]+)" ].join('');
		    var v = this.qs.match(new RegExp(p, "i"));

		    if(v == null || v.length < 1){
			    return "";
		    }

		    return decodeURIComponent(v[1]);
		};
	};

	var TCS = {
		Uploader : {
			clientId : '',
			clientDomain : '',
			sourceId : '',
			type : '',

			complete : function(result) {
				window.parent.postMessage(JSON.stringify({ sign: 'tcs.message', source: this.sourceId, data: result }), "*");
			},

			buildHtml : function() {
				var html = [];

				html.push('<input type="hidden" id="clientId" name="clientId" value="' + this.clientId + '" />');
				html.push('<input type="hidden" id="clientDomain" name="domain" value="' + this.clientDomain + '" />');
				html.push('<input type="hidden" id="sourceId" name="sourceId" value="' + this.sourceId + '" />');
				html.push('<div id="up' + this.type + '"></div>');

				return html.join('\n');
			},

			init : function(type) {
				var p = new Q(location.search);

				this.clientId = p.val('clientId');
				this.clientDomain = p.val('domain');
				this.sourceId = p.val('sourceId');
				this.type = type;

				$(document.body).append(this.buildHtml());

				var _this = this;

				$("#up" + type).gcsUpload({
					width : "100%",
					height : "100%",
					url : "/multi-media/upload/" + type,	// 上传文件的路径
					clientId : _this.clientId,
					clientDomain : _this.clientDomain,
					uploadType : _this.type,

					onSelect: function(files, allFiles) { },
					onDelete: function(file, surplusFiles) { },
					onSuccess: function(file) { },
					onFailure: function(file) { },
					onComplete: function(responseInfo) {
						_this.complete(responseInfo);
					}
				});
			}
		}

	};

	window.TCS = TCS;

})(window);

