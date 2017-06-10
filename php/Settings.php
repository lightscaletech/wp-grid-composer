<?php

class LSGC_SettingsItem {

    public function __construct() {

    }
}

class LGGC_PostTypeItem extends LSGC_SettingsItem {

    public function __construct() {

    }

}



class LSGC_SettingsManager {

    private $pages = array(
        'LSGC_PostTypeItem'
    );

    public function __construct() {

        $this->pages = apply_filters('lsgc_register_settings_page', $this->pages);

        add_action('admin_menus', array($this, "registerMenus"));
    }

    public function registerMenus() {
        foreach ($pages as $p) { new $p(); }
    }
}