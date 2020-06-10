<?php

// autoload_static.php @generated by Composer

namespace Composer\Autoload;

class ComposerStaticInitb4aaa7423febe8ce8e389a69da980335
{
    public static $prefixLengthsPsr4 = array (
        'F' => 
        array (
            'Firebase\\JWT\\' => 13,
        ),
    );

    public static $prefixDirsPsr4 = array (
        'Firebase\\JWT\\' => 
        array (
            0 => __DIR__ . '/..' . '/firebase/php-jwt/src',
        ),
    );

    public static function getInitializer(ClassLoader $loader)
    {
        return \Closure::bind(function () use ($loader) {
            $loader->prefixLengthsPsr4 = ComposerStaticInitb4aaa7423febe8ce8e389a69da980335::$prefixLengthsPsr4;
            $loader->prefixDirsPsr4 = ComposerStaticInitb4aaa7423febe8ce8e389a69da980335::$prefixDirsPsr4;

        }, null, ClassLoader::class);
    }
}