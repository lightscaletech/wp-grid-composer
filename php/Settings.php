<?php

define('LSGC_SETTINGS_RSLUG', 'lsgc_grid_composer');

class LSGC_SettingsItem {

    public function __construct($pageTitle, $menuTitle, $slug) {
        add_submenu_page(LSGC_SETTINGS_RSLUG, $pageTitle, $menuTitle,
                         'manage_options', $slug, array($this, 'pageViewWrap'));

        add_action('admin_init', array($this, 'registerSettings'));
    }

    public function registerSettings() {}

    public function pageViewWrap() {
    ?>
        <div class="wrap">
            <form method="post" action="options.php">
                <h1>Grid composer</h1>
                <?php $this->pageView(); ?>
                <?php submit_button();  ?>
            </form>
        </div>
    <?php
    }

    protected function pageView() {
        ?>
        <div class="wrap">
            BASE PAGE VIEW. <br/>
            Please override <pre>LSGC_SettingsItem::pageView()</pre>
        </div>
        <?php
    }
}

class LSGC_PostTypeItem extends LSGC_SettingsItem {

    const slug = LSGC_SETTINGS_RSLUG;
    const optName = 'post_types';

    private $opts;

    public function __construct() {
        parent::__construct("Post Types", "Post Types", static::slug);
    }

    public function pageView() {
        ?>
        <h2>Post types</h2>

        <?php
        $this->opts = get_option(static::optName);
        settings_fields(LSGC_SETTINGS_RSLUG);
        do_settings_sections(static::slug);
    }

    private function removePts($pts){
        $to_remove = array('attachment', 'revision', 'nav_menu_item',
                           'custom_css', 'customize_changeset');
        $new = array();
        foreach($pts as $pt) {
            if (!in_array($pt, $to_remove)) {
                $new[] = $pt;
            }
        }
        return $new;
    }

    public function registerSettings() {
        $pts = $this->removePts(get_post_types());

        register_setting(LSGC_SETTINGS_RSLUG, static::optName,
                         array($this, 'sanatizeSettings'));

        add_settings_section('lsgc_set_post_types',
                             'Enabled Post Types',
                             array($this, 'enPtSection'),
                             static::slug);

        foreach($pts as $pt){
            add_settings_field("pt_{$pt}", $pt,
                               array($this, 'ptField'), static::slug,
                               'lsgc_set_post_types',
                               array('pt' => $pt));
        }
    }

    public function sanatizeSettings($input) {
        $new = array();

        foreach ($input as $key => $val) {
            $new[$key] = (intval($val) > 0) ? true : false;
        }

        return $new;
    }

    public function enPtSection() {
        echo "<p>Select the post types you want to use grid composer on by " .
             "default. <i>This can be switched when editing a post,</i></p>";
    }

    public function ptField($args) {
        $id = isset($args['pt']) ? $args['pt'] : '';
        $checked = isset($this->opts[$id]) ? $this->opts[$id] : false;
        $checked = $checked ? 'checked="checked"' : '';
        $optName = static::optName;
        echo "<input id=\"pt_{$id}\" type=\"checkbox\" value=\"1\" " .
             "name=\"{$optName}[{$id}]\" {$checked} />";
    }

}



class LSGC_SettingsManager {

    private $pages = array(
        'LSGC_PostTypeItem'
    );

    public function __construct() {
        add_action('admin_menu', array($this, 'registerMenus'));
    }

    public function registerMenus() {

        add_menu_page('Grid Composer Settings', 'Grid Composer',
                      'manage_options', LSGC_SETTINGS_RSLUG);

        $this->pages = apply_filters(
            'lsgc_register_settings_page', $this->pages);

        foreach ($this->pages as $p) { new $p(); }
    }
}
