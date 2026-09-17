FILESEXTRAPATHS:prepend := "${THISDIR}/../files:"

SRC_URI:append:imx6u-lcd = " \
    file://imx6dl-imx6u-lcd.dts \
    file://imx6dl-imx6u-lcd-u-boot.dtsi \
    file://imx6u_lcd_defconfig \
    file://imx6u-lcd.c \
    file://imx6u-lcd.h \
"

do_configure:prepend:imx6u-lcd() {
    install -m 0644 ${UNPACKDIR}/imx6dl-imx6u-lcd.dts ${S}/arch/arm/dts/
    install -m 0644 ${UNPACKDIR}/imx6dl-imx6u-lcd-u-boot.dtsi ${S}/arch/arm/dts/
    install -m 0644 ${UNPACKDIR}/imx6u_lcd_defconfig ${S}/configs/
    install -m 0644 ${UNPACKDIR}/imx6u-lcd.c ${S}/board/freescale/mx6sabresd/mx6sabresd.c
    install -m 0644 ${UNPACKDIR}/imx6u-lcd.h ${S}/include/configs/mx6sabresd.h
}