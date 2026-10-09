DELTA_KERNEL_DEFCONFIG:append:imx6u-lcd = " imx6u-lcd.config"

do_kernel_configme[dirs] += "${UNPACKDIR}"

do_copy_defconfig[file-checksums] += "${@' '.join('%s/arch/arm/configs/%s:True' % (d.getVar('S'), name) for name in ('imx_v7_defconfig', 'imx6u-lcd.config')) if d.getVar('EXTERNALSRC') and d.getVar('MACHINE') == 'imx6u-lcd' else ''}"
