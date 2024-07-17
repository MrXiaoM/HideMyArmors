# HideMyArmors

向其它玩家隐藏我的盔甲!

这是从我服务器的基础功能中分离出来的插件，写这个功能起因是有玩家觉得服务器的盔甲太花了，会挡住自己的皮肤，他想别人能看他的皮肤又不想失去盔甲保护。所以这个功能就出现了。

需要前置 `ProtocolLib`，理论支持全版本。

你可以做一个菜单让玩家开启或关闭盔甲隐藏功能，使用你的权限管理插件给予或移除权限即可。

可用权限如下，所有权限的默认状态为关
```
hidemyarmors.hide.armors 隐藏盔甲物品
  hidemyarmors.hide.head (子权限)隐藏头盔物品
  hidemyarmors.hide.chest (子权限)隐藏胸甲物品
  hidemyarmors.hide.legs (子权限)隐藏护腿物品
  hidemyarmors.hide.feet (子权限)隐藏靴子物品
hidemyarmors.hide.hands 隐藏主副手物品
  hidemyarmors.hide.mainhand (子权限)隐藏主手物品
  hidemyarmors.hide.offhand (子权限)隐藏副手物品
```

除此之外，本插件还提供了一个`擦除实体装备物品nbt标签`的功能，默认关闭，如有需要可到配置文件开启。重载命令为 `/hidemyarmors`

该功能可以防止作弊端查看其他玩家装备的lore、附魔等信息，物品类型、皮革套装颜色、CustomModelData、附魔光泽(替换为水下速掘1919级) 等信息依然保留，该功能兼容 MagicCosmetic 魔法时装插件，时装信息将不会被擦除。
