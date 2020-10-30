/**
 * TAL云存储上传SDK
 * @param window
 */
(function(window){
	// 
    var document = window.document;

	/**
	 * 简单的对象选择器
	 * 仅支持通过 id 获取 DOM 对象
	 */
	var F = function(_selector) {
		var sltr = _selector.trim();

		if(sltr.substr(0, 1) == '#') {
			return new O(document.getElementById(sltr.substr(1)));
		}

		return null;
	};

    /**
     * 去除字符串头尾空格
     * @returns
     */
	String.prototype.trim = function() {
		return this.replace(/(^\s*)|(\s*$)/g, '');
	};

	/**
	 * HTML DOM 元素简单操作封装 
	 */
	function O(o) {
		this.obj = (o !== undefined && o !== null) ? o : null;

		this.isNull = function() {
			return (this.obj === null);
		};

		/**
		 * 判断对象是否包含某 CSS 类
		 */
		this.hasClass = function(clazz) {
			return this.obj.className.match(new RegExp('(\\s|^)' + clazz + '(\\s|$)'));
		};

		/**
		 * 为对象添加 CSS 类
		 */
		this.addClass = function(clazz) {
			if(!this.hasClass(clazz)) {
				this.obj.className += " " + clazz;
			}
		};

		/**
		 * 删除对象的 CSS 类
		 */
		this.removeClass = function(clazz) {
	        if(this.hasClass(clazz)) {
	            this.obj.className = this.obj.className.replace(new RegExp('(\\s|^)' + clazz + '(\\s|$)'), ' ');
	        }
		};

		/**
		 * 获取对象的属性值，或者为对象设置属性
		 */
		this.attr = function() {
			switch(arguments.length) {
			case 1:
				var name = arguments[0];

				return this.obj[name];

			case 2:
				var name = arguments[0];
				var val = arguments[1];

				this.obj[name] = val;

			default: break;
			}

			return;
		};

		/**
		 * 获取对象的 CSS 属性，或者为对象设置 CSS 属性
		 */
		this.css = function() {
			switch(arguments.length) {
			case 1:
				var name = arguments[0];

				return this.obj.style[name];

			case 2:
				var name = arguments[0];
				var val = arguments[1];

				this.obj.style[name] = val;

			default: break;
			}

			return;
		};

		/**
		 * 获取对象的 innerHTML 属性值
		 * 当传入参数为 string 类型时，将参数赋值给对象的 innerHTML 属性
		 */
		this.html = function() {
			if(arguments.length > 0) {
				var html = arguments[0];

				if(html !== undefined && html !== null && (typeof html === 'string')) {
					this.obj.innerHTML = html;
				}

				return;
			}

			return this.obj.innerHTML;
		};

		/**
		 * 触发对象的点击事件，或为对象绑定点击事件
		 */
		this.click = function() {
			if(arguments.length > 0) {
				var cb = arguments[0];

				if(cb !== undefined && cb !== null && (typeof cb === 'function')) {
					var _this = this;

					var _handler = function() {
						cb.apply(_this, null);
					};

					if (this.obj.addEventListener) {
						this.obj.addEventListener('click', _handler, false);

					} else {
						// IE before version 9
						if (this.obj.attachEvent) {
							this.obj.attachEvent('onclick', _handler);
						}
					}
				}

				return;
			}

			this.obj.click();
		};
	}

	/**
	 * TALCloudStorage 封装
	 */
	var TCS = {
		/**
		 * TCS控件数量
		 */
		controls: 0,

		/**
		 * 上传对象封装
		 * 通过引用外部网站的 js 动态创建 iframe 打开外部网站的上传页面，并在 iframe 中实现文件上传的功能
		 */
		Uploader: function() {
			this.args = arguments;
			this.callback = null;
			this.containerId = null;
			this.containerElem = null;
			this.innerContainer = null;
			// 本机环境
            // this.uploadUrl = '//localhost:9080/multi-media/';
            // 测试环境
            this.uploadUrl = '//tcs-en-beta.speiyou.com/multi-media/';
            // 生产环境
			// this.uploadUrl = '//tcs-en.speiyou.com/multi-media/';
			this.iframeId = null;
			this.iframeUrl = '';
			this.options = null;

			/**
			 * 全局的消息监听函数
			 * 用于监其他窗口（或自己）通过 postMessage 发送的消息（IE8+ 支持，Safari，Chrome，FF 均支持）
			 * 采用这种方式实现，主要是解决两个窗口通信时，遇到的跨域问题
			 * @param e
			 */
			this.onMessage = function(e) {
				if(this.callback) {
					var data = JSON.parse(e.data);

					if(data.sign !== undefined && data.sign !== null
						&& data.sign === 'tcs.message' && data.source === this.containerId) {
						this.callback.call(this, JSON.parse(data.data));
					}
				}

				this.showContainer(false);
			};

			/**
			 * 设置全局消息监听
			 */
			this.handleMessage = function() {
				var _this = this;

				var _handler = function() {
//					_this.unhandleMessage(_handler);
					_this.onMessage.apply(_this, arguments);
				};

				if (window.addEventListener) {
					window.addEventListener('message', _handler, false);

				} else {
					// IE before version 9
					if (window.attachEvent) {
						window.attachEvent('onmessage', _handler);
					}
				}
			};

			/**
			 * 取消全局消息监听
			 * @param _handler
			 */
			this.unhandleMessage = function(_handler) {
				if (window.removeEventListener) {
					window.removeEventListener('message', _handler, false);

				} else {
					// IE before version 9
					if (window.detachEvent) {
						window.detachEvent('onmessage', _handler);
					}
				}
			};

			/**
			 * 创建容器
			 */
			this.buildContainer = function() {
				var frag = document.createDocumentFragment();
				var div = document.createElement('div');

				div.setAttribute('id', this.containerId);
				div.setAttribute('class', 'gucontainer hide');

				div.innerHTML = '<div class="caption"><span id="' + this.containerId + '_close">X</span>' + this.options.title + '</div>' + this.buildIframe();

				frag.appendChild(div);

				document.body.appendChild(frag);
			};

			/**
			 * 创建 iframe
			 * @returns
			 */
			this.buildIframe = function() {
				var iframe = [];

				iframe.push('<iframe');
				iframe.push('id="' + this.iframeId + '"');
				iframe.push('class="guiframe"');
				iframe.push('border="0"');
				iframe.push('frameborder="0"');
				iframe.push('width="100%"');
				iframe.push('height="100%"');
				iframe.push('src="' + this.iframeUrl + '"');
				iframe.push('></iframe>');

				return iframe.join(' ');
			};

			/**
			 * 显示或隐藏容器
			 * @param show
			 */
			this.showContainer = function(show) {
				if(this.innerContainer) {
					if(!this.containerElem) {
						this.containerElem = F('#' + this.containerId);

						var _this = this;

						F('#' + _this.containerId + '_close').click(function(){
							_this.showContainer(false);
						});
					}

					if(show) {
						var x = 0, y = 0;

						if(this.options.center) {
							var doc = document.documentElement, body = document.body;
							var w = doc.clientWidth, h = doc.clientHeight;

							/**
							 * 在 IE，FireFox，以及 Chrome 中，documentElement 与 body 的滚动条位置信息是互斥的
							 * 通过 max 方法来获取真实滚动位置，以实现这几个主流浏览器的兼容
							 */
							x = (w - 400) / 2 + Math.max(doc.scrollLeft, body.scrollLeft);
							y = (h - 300) / 2 + Math.max(doc.scrollTop, body.scrollTop);

							/**
							 * 如果是在iframe中，则需要就是iframe本身的偏移量，以得出屏幕居中的位置
							 */
							if(window.frameElement) {
								doc = window.parent.document.documentElement;
								body = window.parent.document.body;

								h = doc.clientHeight;

								var e = window.frameElement;
								var top = e.offsetTop;

								while(e.offsetParent) {
									e = e.offsetParent;
									top += e.offsetTop;
								}

								y = (h - 300) / 2 + Math.max(doc.scrollTop, body.scrollTop) - top;
							}

						} else {
							/**
							 * FireFox 没有 window.event 对象
							 * 通过遍历顶级事件方法，来获取 event 对象，并返回给 window 对象设置 event 属性
							 * 以此模拟实现 window.event
							 */
							if(!window.event) {
								var E = function() { var c = E.caller; while(c.caller) c = c.caller; return c.arguments[0]; };

								__defineGetter__("event", E);
							}

							var we = window.event || null;
							var elem = we.srcElement || null;

							if(!elem) {
								elem = we.target;
							}

							x = elem.offsetLeft;
							y = elem.offsetTop + elem.offsetHeight;
						}

						this.containerElem.css('left', '' + x + 'px');
						this.containerElem.css('top', '' + y + 'px');

						this.containerElem.removeClass('hide');

					} else {
						this.containerElem.addClass('hide');
					}
				}
			};

			/**
			 * 控件初始化完成之后，每次上传之前，必须调用该方法
			 */
			this.upload = function() {
				F('#' + this.iframeId).attr('src', this.iframeUrl);

				this.showContainer(true);
			};

			/**
			 * 初始化控件
			 */
			this.init = function() {
				/**
				 * TCS控件数量
				 */
				TCS.controls ++;

				var _callback = null;
				var _containerId = '';
				/**
				 * 上传控件的自定义选项，必要的选项是 clientId；
				 * 默认情况下 type='image'
				 * center 参数仅在没有指定容器ID的情况下生效
				 */
				var _options = { clientId: '', type: 'image', title: '上传文件', center: true };

				/**
				 * 初始化参数：容器ID，回调函数，自定义选项
				 * 该方法最多 3 个参数，其中回调函数是必要参数，其余两个是可选参数
				 */
				switch(this.args.length) {
				case 3:
					var arg3 = this.args[2];

					if(arg3 !== undefined && arg3 !== null && (typeof arg3 === 'object')) {
						for(p in arg3) {
					        if (arg3.hasOwnProperty(p)) {
					        	_options[p] = arg3[p];
					        }
					    }
					}

				case 2:
					var arg2 = this.args[1];

					if(arg2 !== undefined && arg2 !== null) {
						if(typeof arg2 === 'object') {
							for(p in arg2) {
						        if (arg2.hasOwnProperty(p)) {
						        	_options[p] = arg2[p];
						        }
						    }

						} else if(typeof arg2 === 'function') {
							_callback = arg2;
						}
					}

				case 1:
					var arg1 = this.args[0];

					if(arg1 !== undefined && arg1 !== null) {
						if(typeof arg1 === 'function') {
							_callback = arg1;

						} else if(typeof arg1 === 'string') {
							_containerId = arg1.trim();
						}
					}
					break;

				default: break;
				}

				/**
				 * 初始化控件属性值
				 */
				this.callback = _callback;
				this.containerId = _containerId;
				this.containerElem = F('#' + _containerId);
				this.innerContainer = this.containerElem.isNull();
				this.iframeId = _containerId + '_iframe';
				this.iframeUrl = this.uploadUrl + _options.type + '/index.html?clientId=' + _options.clientId + '&domain=' + encodeURIComponent(document.domain) + '&sourceId=' + (this.containerId || '');
				this.options = _options;

				/**
				 * 如果未指定容器ID，就创建新容器
				 */
				if(this.innerContainer) {
					this.containerElem = null;
					this.containerId = 'gu' + (new Date().getTime()) + 'c' + TCS.controls;
					this.iframeId = this.containerId + '_iframe';
					this.iframeUrl += this.containerId;

					this.buildContainer();

				} else {
					/**
					 * 否则，在容器中插入 iframe
					 */
					this.containerElem.html(this.buildIframe());
				}

				if(this.callback) {
					this.handleMessage();
				}
			};

			/**
			 * 初始化
			 */
			this.init();
		}
	};

	window.TCS = TCS;

})(window);
