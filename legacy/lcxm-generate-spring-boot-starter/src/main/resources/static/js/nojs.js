/*
 * 旨在消灭项目中重复的JS代码;
   核心思想是:约定优于配置.
   即在元素做做特别的约定(标识)就可以实现通用的功能,而不需要额外的通过js代码去实现这些功能.
   文件划分:
   nojs.js 文件: 理应做为做基本的文件,实现最通用的功能,理应每个项目都可以无缝引入的功能,理应放在head中引入
   nojs-module-moduleName.js 文件: 主要为某一些项目某些某块特定处理的功能,分多文件,按需引入
   @author Vic.xu
   @since terrason 
 */
(function(windom, $) {
	/*
	 * 在nojs.init执行之前执行的函数 在模块加载前运行代码 不要放在ready中
	  使用方式: ($nojs.after同理)
	  $nojs.before(function(context){
	      this; //表示nojs全局对象，等同于$nojs.
	      this.test.enable=false;    //禁用test模块
	      this.chosen.enable=false;   //禁用chosen模块
		  //do something else
	  });
	 * 
	*/
	var before = [];
	var after = [];
	window.base_url = windom.base_url || "";
	windom.storage = windom.storage || {get:function(){}};
	windom.project_prefix = windom.project_prefix ||"/";
	var $nojs = function(context) {
		//在初始化nojs的各个模块之前执行的方法
		for (var i in before) {
			if ($.isFunction(before[i])) {
				if (before[i].call($nojs, context) === false) {
					return;
				}
			}
		}
		//针对模块的优先级进行一次排序操作   priority  越小  越先执行
		var modules = [];
		var priorityDefault = 99; //默认的优先级
		for (var name in $nojs) {
			var app = $nojs[name];
			if (!app.priority && 0 != app.priority) {
				app.priority = priorityDefault;
			}
			app.name = name;
			modules.push(app);
		}
		modules.sort(function(p1, p2) {
			return p1.priority - p2.priority;
		}).forEach(function(app) {
			if ($.isFunction(app.enable) ? app.enable() : app.enable === true) {
				if ($nojs.debug) {
					console.debug(app.name + " ->" + app.priority)
				}
				app.init(context);
			}
		});
		//在初始化nojs的各个模块之后执行的方法
		for (var i in after) {
			$.isFunction(after[i]) && after[i].call($nojs, context);
		}
	};
	$nojs.before = function(fn) {
		before.push(fn);
	}
	$nojs.after = function(fn) {
		after.push(fn);
	}
	$nojs.debug = false;

	/*
	 * 000. 测试 所有约定的写法遵照此格式写法
	 */
	$nojs.test = {
		priority: 100,
		// 1-是否启用此约定,可以根据依赖模块判断或直接返回 boolean
		enable: function(context) {
			return true;
		},
		//2-选择器,对哪些元素做一些操作
		selector: ".test",
		//3-对元素做哪些具体的操作
		init: function(context) {
			$(this.selector, context).each(function(i, t) {
				$(t).append("<h1>这是一个测试</h1>")
			});
		}
	};
	/*
	 * 001. select值绑定
	 * 自动选择下拉框的值, 需要在下拉框上绑定当前值 data-value="value"
	 */
	$nojs.selectValueInitialization = {
		enable: true,
		selector: "select[data-value]",
		priority:98,//保证比select2之类的先初始化
		event: "$nojs-select-initialize",
		init: function(context) {
			$(this.selector, context).on(
				this.event,
				function(event) {
					var $select = $(this);
					var value = $select.data("value");
					if (value !== undefined) { // 把相应的值选中
						$select.children("option:selected").prop("selected", false);
						if (value !== '') {
							if(!$select[0].hasAttribute("multiple")) {
								$select.children('option[value="' + value + '"]').prop("selected", true);
							}else {//如果是多选则把value用英文逗号分隔后匹配选择
								value.toString().split(",").forEach(function(item){
									$select.children('option[value="' + item + '"]').prop("selected", true);
								});
							} 
						}
					}
					$select.trigger("change");
				}).trigger(this.event);
		}
	};

	/*
	 * 002. 日历控件
		把datetime样式的元素初始化成bootstrap的日期控件
	 */
	$nojs.datetimepicker = {
		enable: function() {
			return !!$.fn.datetimepicker;
		},
		options: {
			autoclose: true,
			todayBtn: true,
			language: "zh-CN"
		},
		selector: ".datetime",
		initRange: function(option, $input, context) {
			function _bindDateTarget(opt) {
				var method = {
					"startDateTarget": "setStartDate",
					"endDateTarget": "setEndDate"
				};
				if (option[opt] === 'now') {
					$input.datetimepicker(method[opt], new Date());
					return;
				}
				var $target = $(option[opt], context);
				if (!$target.length) {
					$target = $("[name=" + option[opt] + "]", context);
				}
				$target.on("changeDate", function(ev) {
					$input.datetimepicker(method[opt], this.value);
				});
			}
			if (!option.startDateTarget && !option.endDateTarget) {
				return;
			}
			var $form = $input.closest("form");
			if ($form.length) {
				context = $form;
			}

			if (option.startDateTarget) {
				_bindDateTarget("startDateTarget");
			}
			if (option.endDateTarget) {
				_bindDateTarget("endDateTarget");
			}
		},
		init: function(context) {
			var module = this;
			$(this.selector, context).each(function(i, dt) {
				var $input = $(dt);
				var option = $.extend({}, module.options, $input.data());
				$input.datetimepicker(option);
				module.initRange(option, $input, context);

				$input.next("span").click(function() {
					$input.focus();
				});
			});
		}
	};

	/*
	 * 004. 日历
	 * 把calendar样式的div元素初始化成bootstrap的日期控件
	 */
	$nojs.timeCalendar = $.extend(true, {}, $nojs.datetimepicker, {
		selector: "div.calendar"
	});



	/*
	 * 005. CheckBox 和全选/取消 和相关批量操作按钮 的关系建立
	 *  1-全选按钮data-member="subName"  和 data-leader="subName" 的元素关联起来
	 *  2-操作按钮 data-checkbox-required="subName" 是否可以操作(是否选择name="subName" 子元素)
	 */
	$nojs.checkboxBinding = {
		enable: true,
		selector: "[data-member]",
		$member: function($leader, context) { // 获取成员
			var memberName = $leader.data("member"); // 成员name
			// return $("input:checkbox[name=" + memberName + "]:not(:disabled)",	context);
			return $("input:checkbox[data-leader=" + memberName + "]:not(:disabled)",	context);
		},
		$relate: function($leader, $member, context) { // 相关操作按钮是否可操作
			var memberName = $leader.data("member"); // 成员name
			var $relate = $("[data-checkbox-required=" + memberName + "]",
				context)
			if ($relate.length !== 0) {
				$relate.prop("disabled",
					$member.filter(":checked").length === 0);
			}
		},
		init: function(context) {
			var module = this;
			$(this.selector, context).each(function() {
				var $leader = $(this);
				var $member = module.$member($leader, context);
				$leader.change(function() { // 点击全选或者取消
					$member.prop("checked", $leader.prop("checked"));
					module.$relate($leader, $member, context);
				});
				$member.change(function() { // 点击成员 若全选则全选checkbox选中
					$leader.prop("checked", $member.length == $member
						.filter(":checked").length);
					module.$relate($leader, $member, context);
				});
				module.$relate($leader, $member, context);
			});
		}
	};

	/*
	 * 006.chosen 下拉框初始化
	 */
	$nojs.chosen = {
		enable: function() {
			return !!$.fn.chosen;
		},
		selector: "select.chosen",
		option: {
			no_results_text: "没有找到", // 找不到结果时候显示的内容
			allow_single_deselect: true, // 是否允许取消选择
			max_selected_options: 12
			// 当select为多选时，最多选择个数
		},
		init: function(context) {
			var module = this;
			$(module.selector, context).each(function() {
				var $select = $(this);
				var opts = $.extend(module.option, $select.data());
				$select.chosen(opts);
			});
		}
	};

	/*
	 * 007. 发送短信验证码
	 * data-smscode 的按钮点击产生倒计时效果
	 */
	$nojs.smscode = {
		enable: function() {
			return !!$.timer;
		},
		selector: "button[data-smscode],a[data-smscode]",
		init: function(context) {
			var thisModule = this;
			// 短信验证码

			$(document).on("click", thisModule.selector, function() {
				var $this = $(this);
				var originalCaption = $this.text();
				var $caption = $("<span></span>");
				var remain = 60;
				var $remain = $("<span></span>").text(remain);
				$caption.append($remain).append(" 秒后才可重新获取");
				$this.html($caption);
				var timer = $.timer(function() {
					if (remain > 0) {
						$remain.text(--remain);
					} else {
						timer.stop();
						$(thisModule.selector, context).prop(
							"disabled", false);
						$this.text(originalCaption);
					}
				}, 1000, true);
				$(thisModule.selector, context).prop("disabled", true);
			});
		}
	};

	/**
	 * 008.icon-picker初始化
	 */
	$nojs.iconPicker = {
		enable: function() {
			return !!$.fn.iconPicker;
		},
		selector: 'input.icon-picker',
		init: function(context) {
			$(this.selector, context).iconPicker();
		}
	};

	/**
	 * 009. 异步构建一棵树  通过ztree
	 * url:  data-url="" 数据加载的url
	 * setting : data-setting='{"k1":"v1", "k2":"v2"}' 用data绑定json数据: 外部单引号 内部双引号; 也可以是一个变量(不加引号)
	 * after:tree初始化后执行的方法,参数为当前树data-after="fnName" 
	 * checked:绑定当前应该选中的节点 data-checked="idValue" 
	 * http://www.treejs.cn/v3/api.php
	 */
	$nojs.buildZtree = {
		enable: function() {
			return !!$.fn.zTree;
		},
		selector: 'ul.ztree:not(.self)',
		defaultZtreeSetting: { //默认的ztree设置
			view: {
				showLine: true,
				selectedMulti: false
			},
			data: {
				simpleData: {
					enable: true,
					idKey: "id",
					pIdKey: "pid",
					rootPId: "0"
				},
				key: {
					url: "_url" //把url设置成一个不存在的key
				}
			}
		},
		//把setting中的一些方法对应的字符串解析成function
		settingString2Fn: function(opts) {
			var callback = opts.callback;
			//把callback中的字符串解析成函数
			if (callback) {
				for (var k in callback) {
					callback[k] = eval(callback[k]);
				}
			}
			//把callviewback中的字符串解析成函数 因为只有部分是function,因此用-fn作为后缀的才表示是函数
			var view = opts.view;
			if (view) {
				for (var k in view) {
					if (k.indexOf('-fn') > -1) {
						var v = view[k];
						var index = k.indexOf('-fn');
						var newK = k.substring(0, index);
						view[newK] = eval(v); //加入新的key
						delete view[k]; //删除老的key

					}
					callback[k] = eval(callback[k]);
				}
			}
		},
		init: function(context) {
			var module = this;
			$(module.selector, context).each(function() {
				var $ul = $(this);
				var url = $ul.data("url");
				if (!url) return;
				var setting = eval($ul.data("setting")) || {};
				module.settingString2Fn(setting);
				var afterFn = $ul.data("after");
				var checked = $ul.data("checked");
				$request.get(url, {}, function(result) {
					setting = $.extend(true, module.defaultZtreeSetting, setting);
					var zTreeObj = $.fn.zTree.init($ul, setting, result.data);
					$ul.data("ztree", zTreeObj);
					if (checked) { //把需要选中的节点选中
						zTreeObj.selectNode(zTreeObj.getNodeByParam("id", curId, null));
					}
					if (afterFn) { //初始化完成执行 的函数
						common.callFunction(afterFn, zTreeObj);
					}
				});
			});
		}
	};

	/**
	 * 010. select2 初始化
	 * https://select2.org/
	 */
	$nojs.select2 = {
		enable: function() {
			return !!$.fn.select2;
		},
		selector: 'select.select2',
		options: {
			allowClear: true,
			language: 'zh-CN'
		},
		init: function(context) {
			var module = this;
			$(module.selector, context).each(function() {
				var $select = $(this);
				var opts = $.extend(true, {}, module.options, $select.data());
				$select.select2(opts);
			});
		}
	};
	
	/**
	 * 011. 滑块 尽量使用data绑定参数
	 * http://ionden.com/a/plugins/ion.rangeSlider/start.html
	 */
	$nojs.slider = {
		enable: function(){
			return !!$.fn.ionRangeSlider;
		},
		selector: "input.slider",
		init: function(context){
			$(this.selector, context).each(function(){
				var $input = $(this);
				$input.ionRangeSlider();
			});
		}
	};
	
	/**
	 * 012. bootstrap-select https://www.bootstrapselect.cn/index.htm
	 * selectpicker
	 */
	$nojs.selectpicker = {
		enable: function(){
			return !!$.fn.selectpicker;
		},
		selector: "select.selectpicker", // 加此样式 会被插件本身初始化, 但是由于一些数据的加载顺序 此处重新初始化一次
		init: function(context){
			$(this.selector, context).each(function(){
				$(this).selectpicker();
			});
		}
	};
	
	
	windom.$nojs = $nojs;

})(window, jQuery);
$(document).ready(function() {
	console.info("into nojs");
	window.$nojs(document);
});
