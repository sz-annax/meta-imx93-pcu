FILESEXTRAPATHS:prepend := "${THISDIR}/../files:"

SRC_URI:append:imx6u-lcd = " file://imx6dl-imx6u-lcd.dts file://imx6u-lcd-linux.dtsi file://imx6u-lcd.cfg"
DELTA_KERNEL_DEFCONFIG:append:imx6u-lcd = " imx6u-lcd.cfg"

do_configure:prepend:imx6u-lcd() {
    install -m 0644 ${UNPACKDIR}/imx6dl-imx6u-lcd.dts ${S}/arch/arm/boot/dts/nxp/imx/
    install -m 0644 ${UNPACKDIR}/imx6u-lcd-linux.dtsi ${S}/arch/arm/boot/dts/nxp/imx/
    printf '\n#include "imx6u-lcd-linux.dtsi"\n' >> ${S}/arch/arm/boot/dts/nxp/imx/imx6dl-imx6u-lcd.dts
}