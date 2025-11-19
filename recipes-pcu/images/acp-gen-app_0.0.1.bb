SUMMARY = "ACP app upgrade package generator"
DESCRIPTION = "Generates ACP upgrade tarball from existing deploy images"
LICENSE = "CLOSED"

SRC_URI = "file://gen_pcu_app.py"

S = "${UNPACKDIR}"

inherit python3native


TARGET_NAME ?= "ACP"
TARGET_TYPE ?= "ACP"
IMAGE_TYPE ?= "deb"
DEB_FILES = 'acp-app,device-service'

IMAGES_DIR ?= "${DEPLOY_DIR_IMAGE}"
OUT_DIR ?= "${TMPDIR}/annax"

do_compile() {
    echo "==== deb package generation ===="
    echo "IMAGES_DIR = ${IMAGES_DIR}"
    echo "OUT_DIR    = ${OUT_DIR}"

    if [ ! -d "${IMAGES_DIR}" ]; then
        bbfatal "Image directory not found: ${IMAGES_DIR}"
    fi

    ${PYTHON} ${UNPACKDIR}/gen_pcu_app.py \
        --output-dir "${OUT_DIR}" \
        --target-version "${PV}" \
        --target-name "${TARGET_NAME}" \
        --target-type "${TARGET_TYPE}" \
        --deploy-dir-deb "${DEPLOY_DIR_DEB}/${PACKAGE_ARCH}" \
        --deb-list "${DEB_FILES}"
}

do_install[noexec] = "1"
do_compile[nostamp] = "1"
