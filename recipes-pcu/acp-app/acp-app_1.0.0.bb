SUMMARY = "ACP Python Application Service"
DESCRIPTION = "ACP Python application that runs as a systemd service."
LICENSE = "CLOSED"

SRC_URI = "git://git@github.com/sz-annax/acp-app.git;protocol=ssh;branch=main \
           file://acp-app.service \
           file://acp-app.sh \
        "

SRCREV = "eb3b5ba0201a8208adef586772a7d01dd623f281"

S = "${WORKDIR}/git"

do_install() {
    rm -rf ${S}/docs ${S}/script

    # /usr/share/acp-app
    install -d ${D}${datadir}/${PN}
    cp -r ${S}/* ${D}${datadir}/${PN}/

    # /usr/bin/acp-app.sh
    install -d ${D}${bindir}
    install -m 0755 ${UNPACKDIR}/${PN}.sh ${D}${bindir}

    # systemd service
    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${UNPACKDIR}/${PN}.service ${D}${systemd_system_unitdir}/
}

FILES_${PN} = "${bindir}/${PN}.sh"

inherit systemd

SYSTEMD_SERVICE:${PN} = "acp-app.service"
SYSTEMD_AUTO_ENABLE:${PN} = "enable"
