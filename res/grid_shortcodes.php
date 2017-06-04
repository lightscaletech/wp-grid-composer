<?php

class GridShorcodes {
    const COLS = 12;

    public function __construct() {
        $this->registerShortcodes();
    }

    private function registerShortcodes() {
        for ($i = 1; $i <= GridShorcodes::COLS; $i++) {
            echo $i;
            add_shortcode("lsgc_col_{$i}", array($this, 'column'));
        }

        add_shortcode('lsgc_row', array($this, 'row'));
    }

    public function column($attrs, $content, $name) {
        $col_num = end(explode($name, '_'));
        $c  = "<div class=\"lsgc_grid_col_{$col_num}\">";
        $c .= do_shortcode($content);
        $c .= "</div>";
        return $c;
    }

    public function row($attrs, $content) {
        $col_num = end(explode($name, '_'));
        $c  = "<div class=\"lsgc_row\">";
        $c .= do_shortcode($content);
        $c .= "</div>";
        return $c;
    }

}