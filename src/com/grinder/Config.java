package com.grinder;

public class Config {

	/**
	 * Is the server running in production mode?
	 */
	public static boolean PRODUCTION = false;

	/**
	 * Test world
	 */
	public static boolean DEVELOPER_MACHINE = false;
	public static boolean DEBUG_COMBAT_BY_FORCEHAT = false;
	public static boolean enable_debug_messages = true;
	public static boolean enable_player_benchmarking = false;
	public static boolean enable_npc_benchmarking = false;
	public static boolean enable_captcha = false;

	public static boolean block_proxy_vpn_tor = false;

	/*
	* Safety measure for staff commands
	 */
	public static boolean trading_enabled = true;
	public static boolean staking_enabled = true;
	public static boolean gambling_enabled = true;
	public static boolean itempicking_enabled = true;
	public static boolean itemdropping_enabled = true;
	public static boolean shopping_enabled = true;
	public static boolean spawn_game_mode_enabled = true;

	public static final String DISCORD_TOKEN = "NTczMzAyMDAzNTMxMzgyNzg0.XeK8kQ.bko1rQl4q2hy74kP2iI7Z5wH37M";
}
