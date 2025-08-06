DESCRIPTION = "Mount /mnt/data/logs to /var/log/journal using systemd mount unit"
LICENSE = "CLOSED"
PR = "r0"

SRC_URI = "file://var-log-journal.mount"

S = "${UNPACKDIR}"

inherit systemd

do_install() {
    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${UNPACKDIR}/var-log-journal.mount ${D}${systemd_system_unitdir}/
}

SYSTEMD_SERVICE:${PN} = "var-log-journal.mount"
SYSTEMD_AUTO_ENABLE:${PN} = "enable"
