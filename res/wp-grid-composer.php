<?php
/*
  Plugin Name: WP Grid Composor
  Description: A drag and drop grid based editor posts and pages.
  Version:     0.0.1
  Author:      Lightscale Tech Ltd
  Author URI:  http://lightscale.co.uk/
  License:     GPL2
  License URI: https://www.gnu.org/licenses/gpl-2.0.html
  Text Domain: lsgc
*/
$dir = plugin_dir_path(__FILE__);

require_once("{$dir}/grid_shortcodes.php");

new GridShorcodes();