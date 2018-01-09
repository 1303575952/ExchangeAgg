<?php
/**
 * Created by PhpStorm.
 * User: DreamBoy
 * Date: 2016/6/1
 * Time: 10:16
 */

$res = array(
    array('id' => 33, 'name' => '444', 'active' => 0, 'user_id' => 1, 'no_of_reports' => 0),
    array('id' => 29, 'name' => 'AAA', 'active' => 1, 'user_id' => 1, 'no_of_reports' => 0),
    array('id' => 20, 'name' => 'aasdasd', 'active' => 1, 'user_id' => 1, 'no_of_reports' => 0)
);

echo json_encode($res);