package wg.util;

public interface IContextCons {

	String login_server = "login_server";
	
	String ly_account = "ly_account";
	
	String parser_manager = "parser_manager";
	
	String robot_param = "robot_param";
	
	String current_player = "current_player";
	
	String event_dispatch_map = "event_dispatch_map";

	String disabled_module_list = "disabled_module_list";
	
	String game_socket = "game_socket";
	
	String robot_mode = "robot_mode";

	String account_logger = "account_logger";

	String account_schema = "account_schema";

	String module_toggle = "module_toggle";

	/**
	 * value:Class<? extends BaseModule>
	 */
	String current_enabled_exclusive_module = "current_enabled_exclusive_module";

	String selected_avatar_index = "selected_avatar_index";
	
}
