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


require_once(LSGC_DIR . "resources.php");
require_once(LSGC_DIR . "shortcode_registry.php");
require_once(LSGC_DIR . "AjaxAPI.php");
require_once(LSGC_DIR . "grid_shortcodes.php");
require_once(LSGC_DIR . "editor.php");

class WPGridComposer {

    private $resources = NULL;
    private $shortcodeRegistry = NULL;

    function __construct() {
        $this->resources = new Resources();
        $this->shortcodeRegistry = new ShortcodeRegistry();
        new ModualAPI($this->shortcodeRegistry);
        new Editor($this->resources);
        new GridShorcodes($this->resources);
    }

}

new WPGridComposer();