<?php

class LSGC_Resources {

    const JS_EDITOR  = 'lsgc_js_editor';

    const CSS_EDITOR = 'lsgc_css_editor';
    const CSS_GRID   = 'lsgc_css_grid';

    function __construct() {
        add_action('wp_enqueue_scripts', array($this, 'register_style_grid'));
        add_action('admin_enqueue_scripts', array($this, 'register_style_grid'));
        add_action('admin_enqueue_scripts', array($this, 'register_style_editor'));
        add_action('admin_enqueue_scripts', array($this, 'register_js_editor'));
    }

    function register_style_grid() {
        wp_register_style(static::CSS_GRID, LSGC_URL . '/css/grid.css');
    }

    function register_style_editor() {
        wp_register_style(static::CSS_EDITOR, LSGC_URL . '/css/editor.css');
    }

    function register_js_editor() {
        wp_register_script(static::JS_EDITOR, LSGC_URL . '/js/editor.js', array(),
                           '0.0.1', TRUE);
    }

    function enqueue_style_grid() { wp_enqueue_style(static::CSS_GRID);}
    function enqueue_style_editor() { wp_enqueue_style(static::CSS_EDITOR);}
    function enqueue_js_editor() { wp_enqueue_script(static::JS_EDITOR);}
}