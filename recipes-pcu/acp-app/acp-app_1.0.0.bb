SUMMARY = "ACP Python Application Service"
DESCRIPTION = "ACP Python application that runs as a systemd service."
LICENSE = "CLOSED"

SRC_URI = "git://git@github.com/sz-annax/acp-app.git;protocol=ssh;branch=main \
           file://acp-app.service \
        "
SRCREV = "1c3349a1dd44fe3a9941ccc5687de0aa1b197394"

S = "${WORKDIR}/git"

do_install() {
    # delete unnecessary files and folders
    rm -rf ${S}/docs ${S}/script

    # /usr/share/acp-app
    install -d ${D}${datadir}/${PN}
    cp -r ${S}/* ${D}${datadir}/${PN}/

    # systemd service
    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${UNPACKDIR}/${PN}.service ${D}${systemd_system_unitdir}/
}

FILES_${PN} = "${bindir}/${PN}.sh"

inherit systemd

SYSTEMD_SERVICE:${PN} = "acp-app.service"
SYSTEMD_AUTO_ENABLE:${PN} = "enable"
