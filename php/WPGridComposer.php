<?php
/*
  Plugin Name: WP Grid Composer
  Description: A drag and drop grid based editor posts and pages.
  Version:     0.0.1
  Author:      Lightscale Tech Ltd
  Author URI:  http://lightscale.co.uk/
  License:     GPL2
  License URI: https://www.gnu.org/licenses/gpl-2.0.html
  Text Domain: lsgc
*/

define('LSGC_DIR', plugin_dir_path(__FILE__));
define('LSGC_URL', plugin_dir_url(__FILE__));


require_once(LSGC_DIR . "Resources.php");
require_once(LSGC_DIR . "Settings.php");
require_once(LSGC_DIR . "ModuleRegistry.php");
require_once(LSGC_DIR . "AjaxAPI.php");
require_once(LSGC_DIR . "GridShortcodes.php");
require_once(LSGC_DIR . "Editor.php");

class LSGC_WPGridComposer {

    private $resources = NULL;
    private $shortcodeRegistry = NULL;

    function __construct() {
        $this->resources = new LSGC_Resources();
        $this->shortcodeRegistry = new LSGC_ShortcodeRegistry();
        new LSGC_SettingsManager();
        new LSGC_ModuleAPI($this->shortcodeRegistry);
        new LSGC_Editor($this->resources);
        new LSGC_GridShorcodes($this->resources);
    }

}

new LSGC_WPGridComposer();