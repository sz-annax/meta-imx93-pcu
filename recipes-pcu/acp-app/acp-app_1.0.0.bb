SUMMARY = "ACP Python Application Service"
DESCRIPTION = "ACP Python application that runs as a systemd service."
LICENSE = "CLOSED"

SRC_URI = "git://git@github.com/sz-annax/acp-app.git;protocol=ssh;branch=main \
           file://acp-app.service \
        "
SRCREV = "3984e04714dd9e137f3673a9c86bef65837e9d8a"

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
