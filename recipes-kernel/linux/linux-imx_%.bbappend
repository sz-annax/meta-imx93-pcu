FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI += " \
    file://devtool-fragment.cfg \
    file://0001-add-imx93-pcu-dts.patch \
    file://0002-add-support-for-ak4619.patch \
"
