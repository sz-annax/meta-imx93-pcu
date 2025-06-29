# meta-mylayer/recipes-support/chrony/chrony_%.bbappend

FILESEXTRAPATHS:prepend := "${THISDIR}/chrony:"

SRC_URI += "file://chrony.conf"

SYSTEMD_SERVICE:${PN} = "chronyd.service"
SYSTEMD_AUTO_ENABLE:${PN} = "enable"

do_install:append() {
    install -d ${D}${sysconfdir}
    install -m 0644 ${UNPACKDIR}/chrony.conf ${D}${sysconfdir}/chrony.conf
}
