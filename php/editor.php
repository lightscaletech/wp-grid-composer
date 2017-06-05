<?php

class Editor {

    private $resources;

    function __construct($resources) {
        $this->resources = $resources;
        add_action('edit_form_after_title', array($this, 'before_editor'));
    }

    function before_editor() {
        $this->resources->enqueue_style_grid();
        $this->resources->enqueue_style_editor();
        $this->resources->enqueue_js_editor();

        echo '<div id="lsgc_editor_container"></div>';
    }
}