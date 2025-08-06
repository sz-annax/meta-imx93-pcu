FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

SRC_URI += "file://journald.conf"

do_install:append() {
    install -d ${D}${sysconfdir}/systemd
    install -m 0644 ${WORKDIR}/sources-unpack/journald.conf ${D}${sysconfdir}/systemd
}
