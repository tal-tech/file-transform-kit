
(function($,undefined){
	$.fn.gcsUpload = function(options, param) {
		var otherArgs = Array.prototype.slice.call(arguments, 1);

		if (typeof options == 'string') {
			var fn = this[0][options];

			if($.isFunction(fn)){
				return fn.apply(this, otherArgs);

			}else{
				throw('gcsUploader - No such method: ' + options);
			}
		}

		return this.each(function(){
			var para = { };	// 保留参数
			var self = this;

			var defaults = {
				width : '600px',	// 宽度
				height : '400px',	// 宽度
				itemWidth : '120px',	// 文件项的宽度
				itemHeight : '120px',	// 文件项的高度
				url : '',	// 上传文件的路径
				multiple : false,	// 是否可以多个文件上传
				dragDrop : false,	// 是否可以拖动上传文件
				del : true,	// 是否可以删除文件
				finishDel : false,	// 是否在上传文件完成后删除预览
				clientId : '',
				clientDomain : '',
				uploadType : 'image',
				allowFileTypes : {
				    image : [
				        'image/jpeg',
				        'image/png',
				        'image/jpg',
				        'image/gif',
				        'image/bmp',
				        'image/tiff'
					],
			    	audio : [
					    'audio/mp3',
					    'audio/mpeg',
					    'audio/x-wav',
					    'audio/x-ms-wma',
					    'audio/x-ms-wmv',
					    'audio/mid',
					    'audio/midi'
					],
		    		video : [
						'video/mp4',
						'video/mpeg',
						'video/x-msvideo',
						'video/3gpp',
						'video/quicktime'
					]
				},
				allowFileExts : {
				    image : [
				        '.png',
				        '.jpg',
				        '.jpeg',
				        '.gif',
				        '.bmp',
				        '.tiff'
					],
			    	audio : [
					    '.mp3',
					    '.amr',
					    '.wav',
					    '.wma',
					    '.mid',
					    '.midi'
					],
		    		video : [
						'.mp4',
						'.mpeg',
						'.mpg',
						'.wmv',
						'.3gp',
						'.avi',
						'.mkv',
						'.rm',
						'.rmvb',
						'.flv',
						'.vod'
					]
				},
				maxFileLength : -1,

				/* 提供给外部的接口方法 */
				onSelect : function(selectFiles, files) { },	// 选择文件的回调方法  selectFile:当前选中的文件  allFiles:还没上传的全部文件
				onDelete : function(file, files) { },		// 删除一个文件的回调方法 file:当前删除的文件  files:删除之后的文件
				onSuccess : function(file) { },	// 文件上传成功的回调方法
				onFailure : function(file) { },	// 文件上传失败的回调方法
				onComplete : function(responseInfo) { },	// 上传完成的回调方法
			};

			para = $.extend(defaults, options);

			this.init = function(){
				this.createHtml();	// 创建组件html
				this.createCorePlug();	// 调用核心js
			};

			/**
			 * 功能：创建上传所使用的html
			 * 参数: 无
			 * 返回: 无
			 */
			this.createHtml = function() {
				var multiple = "";	// 设置多选的参数

				para.multiple ? multiple = 'multiple' : multiple = '';

				var html = [];

				if(para.dragDrop) {
					// 创建带有拖动的html
					html.push('<form id="uploadForm" action="' + para.url + '" method="post" enctype="multipart/form-data">');
					html.push('	 <div class="upload_box">');
					html.push('		<div class="upload_main">');
					html.push('			<div class="upload_choose">');
					html.push('				<div class="convent_choice">');
					html.push('					<div class="andArea">');
					html.push('						<div class="filePicker">点击选择文件</div>');
					html.push('						<input id="fileImage" type="file" size="30" name="fileselect[]" '+ multiple + '>');
					html.push('					</div>');
					html.push('				</div>');
					html.push('				<span id="fileDragArea" class="upload_drag_area">或者将文件拖到此处</span>');
					html.push('			</div>');
					html.push('			<div class="status_bar">');
					html.push('				<div id="status_info" class="info"></div>');
					html.push('				<div class="btns">');
					html.push('					<!--div class="webuploader_pick">继续选择</div-->');
					html.push('					<div class="upload_btn disable">开始上传</div>');
					html.push('				</div>');
					html.push('			</div>');
					html.push('			<div id="preview" class="upload_preview"></div>');
					html.push('		</div>');
					html.push('		<div class="upload_submit">');
					html.push('			<button type="button" id="fileSubmit" class="upload_submit_btn">确认上传文件</button>');
					html.push('		</div>');
					html.push('		<div id="uploadInf" class="upload_inf"></div>');
					html.push('	</div>');
					html.push('</form>');

				}else{
					var imgWidth = parseInt(para.itemWidth.replace('px', '')) - 15;

					// 创建不带有拖动的html
					html.push('<form id="uploadForm" action="' + para.url + '" method="post" enctype="multipart/form-data">');
					html.push('	<div class="upload_box">');
					html.push('		<div class="upload_main single_main">');
					html.push('			<div id="preview" class="upload_preview">');
					html.push('				<div class="add_upload">');
					html.push('					<a style="height:' + para.itemHeight + '; width:' + para.itemWidth + ';" title="点击添加文件" id="rapidAddImg" class="add_imgBox" href="javascript:void(0)">');
					html.push('						<div class="uploadImg" style="width:' + imgWidth + 'px">');
					html.push('							<img class="upload_image" src="../images/select_' + para.uploadType + '.png" style="width:expression(this.width > ' + imgWidth + ' ? ' + imgWidth + 'px : this.width)" onerror="javascript:this.src=\'../images/select_misc.png\';" />');
					html.push('						</div>');
					html.push('					</a>');
					html.push('				</div>');
					html.push('			</div>');
					html.push('			<div class="status_bar">');
					html.push('				<div id="status_info" class="info"></div>');
					html.push('				<div class="btns">');
					html.push('					<input id="fileImage" type="file" size="30" name="fileselect[]" ' + multiple + '>');
					html.push('					<!--div class="webuploader_pick">选择文件</div-->');
					html.push('					<div class="upload_btn disable">开始上传</div>');
					html.push('				</div>');
					html.push('			</div>');
					html.push('		</div>');
					html.push('		<div class="upload_submit">');
					html.push('			<button type="button" id="fileSubmit" class="upload_submit_btn">确认上传文件</button>');
					html.push('		</div>');
					html.push('		<div id="uploadInf" class="upload_inf"></div>');
					html.push('	</div>');
					html.push('</form>');
				}

	            $(self).append(html.join('\n')).css({ 'width': para.width , 'height': para.height });
	            
	            // 初始化html之后绑定按钮的点击事件
	            this.addEvent();
			};

			/**
			 * 功能：显示统计信息和绑定继续上传和上传按钮的点击事件
			 * 参数: 无
			 * 返回: 无
			 */
			this.funSetStatusInfo = function(files){
				var size = 0;
				var num = files.length;

				$.each(files, function(k, v) {
					// 计算得到文件总大小
					size += v.size;
				});
				
				// 转化为kb和MB格式。文件的名字、大小、类型都是可以现实出来。
				if (size > 1024 * 1024) {
					size = (Math.round(size * 100 / (1024 * 1024)) / 100).toString() + 'MB';                

				} else {
					size = (Math.round(size * 100 / 1024) / 100).toString() + 'KB';                
				} 

				// 设置内容
				$("#status_info").html('文件大小：' + size);
//				$("#status_info").html("选中 "+num+" 个文件，共 "+size+"。");
			};

			/**
			 * 功能：过滤上传的文件格式等
			 * 参数: files 本次选择的文件
			 * 返回: 通过的文件
			 */
			this.funFilterEligibleFile = function(files){
				var arrFiles = [];  // 替换的文件数组
				var allowTypes = para.allowFileTypes[para.uploadType] || null;
				var allowExts = para.allowFileExts[para.uploadType] || null;

				for (var i = 0, file; file = files[i]; i ++) {
					var fileType = file.type || '', fileExt = '';
					var n = file.name.lastIndexOf('.');

					if(n != -1) {
						fileExt = file.name.substr(n).toLowerCase();
					}

					console.log(file.name + ", " + fileExt + ", " + fileType);

					if((allowTypes && $.inArray(fileType, allowTypes) == -1) &&
						(allowExts && $.inArray(fileExt, allowExts) == -1)) {
						alert('不支持这种文件格式的上传：\n\n' + file.name);
						break;
					}

					if(para.maxFileLength != -1 && file.size >= para.maxFileLength) {
						alert('文件太大了：\n\n' + file.name);	
						break;
					}

					arrFiles.push(file);	
				}

				return arrFiles;
			};
			
			/**
			 * 功能： 处理参数和格式上的预览html
			 * 参数: files 本次选择的文件
			 * 返回: 预览的html
			 */
			this.funDisposePreviewHtml = function(file, e){
				var imgWidth = parseInt(para.itemWidth.replace('px', '')) - 15;

				// 处理配置参数删除按钮
				var delHtml = '';

				// 显示删除按钮
				if(para.del) {
					delHtml = '<span class="file_del" data-index="'+file.index+'" title="删除"></span>';
				}
				
				// 处理不同类型文件代表的图标
				var fileImgSrc = '../images/fileType/file.png';
				var n = file.name.lastIndexOf('.');

				if(n != -1) {
					fileImgSrc = '../images/fileType/' + file.name.substr(n + 1) + '.png';
				}

				var html = [ ];

				// 图片上传的是图片还是其他类型文件
				if (file.type.indexOf('image') == 0) {
					html.push('<div id="uploadList_'+ file.index +'" class="upload_append_list">');
					html.push('	<div class="file_bar">');
					html.push('		<div style="padding:5px;">');
					html.push('			<p class="file_name">' + file.name + '</p>');
					html.push(delHtml);	// 删除按钮的html
					html.push('		</div>');
					html.push('	</div>');
					html.push('	<a style="height:' + para.itemHeight + ';width:' + para.itemWidth + ';" href="#" class="imgBox">');
					html.push('		<div class="uploadImg" style="width:' + imgWidth + 'px">');				
					html.push('			<img id="uploadImage_' + file.index + '" class="upload_image" src="' + e.target.result + '" style="width:expression(this.width > ' + imgWidth + ' ? ' + imgWidth + 'px : this.width)" onerror="javascript:this.src=\'../images/fileType/file.png\';" />');                                                                 
					html.push('		</div>');
					html.push('	</a>');
					html.push('	<p id="uploadProgress_' + file.index + '" class="file_progress"></p>');
					html.push('	<p id="uploadFailure_' + file.index + '" class="file_failure">上传失败，请重试</p>');
					html.push('	<p id="uploadSuccess_' + file.index + '" class="file_success"></p>');
					html.push('</div>');

				}else{
					html.push('<div id="uploadList_' + file.index + '" class="upload_append_list">');
					html.push('	<div class="file_bar">');
					html.push('		<div style="padding:5px;">');
					html.push('			<p class="file_name">' + file.name + '</p>');
					html.push(delHtml);	// 删除按钮的html
					html.push('		</div>');
					html.push('	</div>');
					html.push('	<a style="height:' + para.itemHeight + ';width:' + para.itemWidth + ';" href="#" class="imgBox">');
					html.push('		<div class="uploadImg" style="width:' + imgWidth + 'px">');
					html.push('			<img id="uploadImage_' + file.index + '" class="upload_image" src="' + fileImgSrc + '" style="width:expression(this.width > ' + imgWidth + ' ? ' + imgWidth + 'px : this.width)" onerror="javascript:this.src=\'../images/fileType/file.png\';" />');                                                                 
					html.push('		</div>');
					html.push('	</a>');
					html.push('	<p id="uploadProgress_' + file.index + '" class="file_progress"></p>');
					html.push('	<p id="uploadFailure_' + file.index + '" class="file_failure">上传失败，请重试</p>');
					html.push('	<p id="uploadSuccess_' + file.index + '" class="file_success"></p>');
					html.push('</div>');
				}
				
				return html.join('\n');
			};
			
			/**
			 * 功能：调用核心插件
			 * 参数: 无
			 * 返回: 无
			 */
			this.createCorePlug = function() {
				var params = {
					fileInput: $('#fileImage').get(0),
					uploadInput: $('#fileSubmit').get(0),
					dragDrop: $('#fileDragArea').get(0),
					url: $('#uploadForm').attr('action'),
					clientId: para.clientId,
					clientDomain: para.clientDomain,

					filterFile: function(files) {
						// 过滤合格的文件
						return self.funFilterEligibleFile(files);
					},

					onSelect: function(selectFiles, allFiles) {
						para.onSelect(selectFiles, allFiles);  // 回调方法

						self.funSetStatusInfo(tcsFile.funReturnNeedFiles());  // 显示统计信息

						var html = '', i = 0;

						// 组织预览html
						var funDealtPreviewHtml = function() {
							file = selectFiles[i];

							if (file) {
								var reader = new FileReader();

								reader.onload = function(e) {
									// 处理下配置参数和格式的html
									html += self.funDisposePreviewHtml(file, e);

									i ++;

									// 再接着调用此方法递归组成可以预览的html
									funDealtPreviewHtml();
								};

								reader.readAsDataURL(file);

							} else {
								// 走到这里说明文件html已经组织完毕，要把html添加到预览区
								funAppendPreviewHtml(html);
							}
						};

						// 添加预览html
						var funAppendPreviewHtml = function(html) {
							// liujt, 2015.6.11
							if(selectFiles.length > 0) {
								$('.add_upload').hide();
								$('.upload_btn').removeClass('disable');
							}

							// 添加到添加按钮前
							if(para.dragDrop){
								$('#preview').append(html);

							}else{
								$('.add_upload').before(html);
							}

							// 绑定删除按钮
							funBindDelEvent();
							funBindHoverEvent();
						};
						
						// 绑定删除按钮事件
						var funBindDelEvent = function() {
							if($('.file_del').length > 0) {
								// 删除方法
								$(".file_del").click(function() {
									tcsFile.funDeleteFile(parseInt($(this).attr("data-index")), true);

									return false;	
								});
							}
							
							if($('.file_edit').length > 0) {
								// 编辑方法
								$('.file_edit').click(function() {
									// 调用编辑操作
									//tcsFile.funEditFile(parseInt($(this).attr("data-index")), true);

									return false;	
								});
							}
						};
						
						// 绑定显示操作栏事件
						var funBindHoverEvent = function() {
							$(".upload_append_list").hover(
								function (e) {
									$(this).find(".file_bar").addClass("file_hover");

								},function (e) {
									$(this).find(".file_bar").removeClass("file_hover");
								}
							);
						};

						funDealtPreviewHtml();		
					},

					onDelete: function(file, files) {
						// 移除效果
						$('#uploadList_' + file.index).hide();

						// 重新设置统计栏信息
						self.funSetStatusInfo(files);
//						console.info("剩下的文件");
//						console.info(files);
//
						// liujt, 2015.6.11
						$('.add_upload').show();
						$('.upload_btn').addClass('disable');
						$("#status_info").html('');
				},

					onProgress: function(file, loaded, total) {
						var eleProgress = $('#uploadProgress_' + file.index), percent = (loaded / total * 100).toFixed(2) + '%';

						if(eleProgress.is(":hidden")){
							eleProgress.show();
						}

						eleProgress.css("width",percent);
					},

					onSuccess: function(file, response) {
						$("#uploadProgress_" + file.index).hide();
						$("#uploadSuccess_" + file.index).show();
						$("#uploadInf").append("<p>上传成功，文件地址是：" + response + "</p>");

						// 根据配置参数确定隐不隐藏上传成功的文件
						if(para.finishDel){
							// 移除效果
							$("#uploadList_" + file.index).fadeOut();

							// 重新设置统计栏信息
							self.funSetStatusInfo(tcsFile.funReturnNeedFiles());
						}
					},

					onFailure: function(file) {
						$("#uploadProgress_" + file.index).hide();
						$("#uploadSuccess_" + file.index).show();
						$("#uploadInf").append("<p>文件" + file.name + "上传失败！</p>");	
						$("#uploadImage_" + file.index).css("opacity", 0.2);
					},

					onComplete: function(response){
						// liujt, 2015.6.11
						para.onComplete(response);
//						console.info(response);
					},

					onDragOver: function() {
						$(this).addClass("upload_drag_hover");
					},

					onDragLeave: function() {
						$(this).removeClass("upload_drag_hover");
					}

				};
				
				tcsFile = $.extend(tcsFile, params);

				tcsFile.init();
			};
			
			/**
			 * 功能：绑定事件
			 * 参数: 无
			 * 返回: 无
			 */
			this.addEvent = function() {
				// 如果快捷添加文件按钮存在
				if($(".filePicker").length > 0) {
					// 绑定选择事件
					$(".filePicker").bind("click", function(e) {
		            	$("#fileImage").click();
		            });
				}
	            
				// 绑定继续添加点击事件
				$(".webuploader_pick").bind("click", function(e) {
	            	$("#fileImage").click();
	            });

				// 绑定上传点击事件
				$(".upload_btn").bind("click", function(e) {
					// 判断当前是否有文件需要上传
					if(tcsFile.funReturnNeedFiles().length > 0) {
						$("#fileSubmit").click();

					}else{
						alert("请先选中文件再点击上传");
					}
	            });

				// 如果快捷添加文件按钮存在
				if($("#rapidAddImg").length > 0) {
					// 绑定添加点击事件
					$("#rapidAddImg").bind("click", function(e) {
						$("#fileImage").click();
		            });
				}
			};

			// 初始化上传控制层插件
			this.init();
		});
	};
})(jQuery);

