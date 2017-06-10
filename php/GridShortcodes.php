<?php

class LSGC_GridShorcodes {
    const COLS = 12;

    private $resources;

    public function __construct($resources) {
        $this->resources = $resources;

        $this->registerShortcodes();
        add_filter('lsgc_register_shortcodes', array($this, "registerModules"));
    }

    private function registerShortcodes() {
        for ($i = 1; $i <= static::COLS; $i++) {
            add_shortcode("lsgc_col_{$i}", array($this, 'column'));
        }

        add_shortcode('lsgc_row', array($this, 'row'));
    }

    public function column($attrs, $content, $name) {
        $this->resources->enqueue_style_grid();
        $name_split = explode('_', $name);
        $col_num = end($name_split);
        $c  = "<div class=\"lsgc_col_{$col_num}\">";
        $c .= '<div class="lsgc_inner">';
        $c .= do_shortcode($content);
        $c .= '</div>';
        $c .= '</div>';
        return $c;
    }

    public function row($attrs, $content) {
        $c  = "<div class=\"lsgc_row\">";
        $c .= do_shortcode($content);
        $c .= "</div>";
        return $c;
    }

    private function makeColMod($num) {
        return
            array(
                'key' => "lsgc_col_{$num}",
                'name' => "Column {$num}",
                'type' => 'structural',
                'tile_classes' => "lsgc_col_{$num}");
    }

    public function registerModules($in) {
        $mods = array();

        for ($i = 1; $i <= static::COLS; $i++){
            $mods[] = $this->makeColMod($i);
        }

        $mods[] = array(
            'key' => 'lsgc_row',
            'name' => 'Row',
            'type' => 'structural',
            'tile_classes' => 'lsgc_row');

        return lsgc_add_modules($in, $mods);
    }
}