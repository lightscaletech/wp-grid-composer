<?php

class AjaxAPI {

    protected $shortcodeRegistry = NULL;

    public function __construct($scr, $actions) {
        $this->shortcodeRegistry = $scr;

        foreach ($actions as $a) {
            add_action("wp_ajax_lsgc_{$a}", array($this, 'handler'));
        }
    }

    public function handler() {
        $n = $_POST['action'];
        $n = str_replace('lsgc_', '', $n);

        $callable = array($this, $n);

        if(is_callable($callable)) {
            $pre_method = array($this, '__pre__');

            if(is_callable($pre_method)) call_user_func($pre_method);
            $res = call_user_func($callable);
            if ($res) wp_send_json($res);
        }
        else {
            $this->not_found($n);
        }

        wp_die();
    }

    public function not_found($n) {
        echo "NOT FOUND: {$n}";
    }
}

class ModualAPI extends AjaxAPI {

    private $actions = array(
        'getModualsSelection',
        'getModualsDisplayIn',
        'getModualEdit');

    public function __construct($scr) {
        parent::__construct($scr, $this->actions);
    }

    public function __pre__() {
        $this->shortcodeRegistry->load();
    }

    public function getModualsSelection() {
        return $this->shortcodeRegistry->getAllSmall();
    }

    public function getModualsDisplayIn() {
        $keys = $_POST['keys'];
        return $this->shortcodeRegistry->getAllIn($keys);
    }

    public function getModualEdit() {
        $key = $_POST['key'];
        return $this->shortcodeRegistry->get($key);
    }

}