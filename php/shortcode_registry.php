<?php

class ShortcodeRegistry {

    /**
     * {
     *   "key" => "",
     *   "name" => "",
     *   "description" => "",
     *   "parameters" =>
     *   [
     *     {
     *       "name" => "",
     *       "description" => "",
     *       "parameter" => "",
     *       "type" => ""
     *     }
     *   ],
     *   "type" => "(structural|functional|content)"
     *   "icon" => "",
     *   "tile_classes" => "",
     *   "editor_classes" => ""
     * }
     */
    private $shortcodes;

    public function __construct() {
        $this->shortcodes = array();
    }

    public function load() {
        $this->shortcodes = apply_filters('lsgc_register_shortcodes',
                                          $this->shorcodes);
    }

    private function contains($keys, $s) {
        $res = array();
        foreach($keys as $k) {
            try {
                $res[$k] = $s[$k];
            }
            catch (Exception $e){
                continue;
            }
        }
        return $res;
    }

    private function mksmall($s) {
        return $this->contains(array('key', 'name', 'description',
                                     'type', 'icon', 'tile_classes'), $s);
    }

    public function getAll() {
        return $this->shortcodes;
    }

    public function getAllSmall() {
        $res = array();

        foreach ($this->shortcodes as $s) {
            $res[] = $this->mksmall($s);
        }
        return $res;
    }

    public function get($k) {
        foreach ($this->shortcodes as $s) {
            if ($s['key'] === $k) return $s;
        }
        return FALSE;
    }

    public function getAllIn($a) {
        $res = array();
        foreach ($a as $k) {
            $r = $this->get($k);
            if($r) $res[] = $this->mksmall($r);
        }
        return $res;
    }
}

function lsgc_add_modual($all, $new) {
    return ($all[] = $new);
}

function lsgc_add_moduals($all, $new) {
    foreach ($new as $m) {
        $all = lsgc_add_modual($all, $new);
    }
    return $all;
}