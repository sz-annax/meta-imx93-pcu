FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

SRC_URI += "file://colors.sh"

do_install:append() {
    install -d ${D}${sysconfdir}/profile.d
    install -m 0755 ${WORKDIR}/colors.sh ${D}${sysconfdir}/profile.d/colors.sh
}
