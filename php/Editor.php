<?php

class LSGC_Editor {

    private $resources;

    function __construct($resources) {
        $this->resources = $resources;
        add_action('edit_form_after_title', array($this, 'before_editor'));
    }

    function before_editor() {
        global $current_screen;

        $this->resources->enqueue_style_grid();
        $this->resources->enqueue_style_editor();
        $this->resources->enqueue_js_editor();

        $cpt = $current_screen->post_type;
        $ptopts = get_option('post_types');
        $ptopts = isset($ptopts) ? $ptopts : array();

        $enabled = isset($ptopts[$cpt]) ? $ptopts[$cpt] : FALSE;

        echo "<script>_lsgc_editor_enabled__ = {$enabled};</script>";
        if ($enabled) {
            echo '<style> #postdivrich {visibility: collapse; height: 0px;}</style>';
        }
        echo '<div id="lsgc_editor_container"></div>';
    }
}