SUMMARY = "PEI App Python Service"
DESCRIPTION = "PEI Python application that runs as a systemd service."
LICENSE = "CLOSED"

SRC_URI = "git://gitee.com/bigstep/pei-app.git;protocol=https;branch=master \
           file://pei-app.service \
           file://pei-app.sh \
        "

SRCREV = "28a69884148dfbfc0016b421e9b5b7d2baaffb20"

S = "${WORKDIR}/git"

do_install() {
    # /usr/share/pei-app
    install -d ${D}${datadir}/pei-app
    cp -r ${S}/* ${D}${datadir}/pei-app/

    # /usr/bin/pei-app.sh
    install -d ${D}${bindir}
    install -m 0755 ${UNPACKDIR}/pei-app.sh ${D}${bindir}

    # systemd service
    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${UNPACKDIR}/pei-app.service ${D}${systemd_system_unitdir}/
}

FILES_${PN} = "${bindir}/pei-app.sh"

inherit systemd

SYSTEMD_SERVICE:${PN} = "pei-app.service"
SYSTEMD_AUTO_ENABLE:${PN} = "enable"
