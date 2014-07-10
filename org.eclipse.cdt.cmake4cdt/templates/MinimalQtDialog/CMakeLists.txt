cmake_minimum_required (VERSION 2.6)

# to see the compiler output
set(CMAKE_VERBOSE_MAKEFILE ON)

project ($(projectName))

find_package ( Qt4 COMPONENTS QtGui QtCore REQUIRED )
add_definitions( ${QT4_DEFINITIONS} )

set( SRC           main.cpp 
                   Dialog.cpp )

set( MOC_H         Dialog.h )
                   
# SET(UIS            $(projectName).ui )
# set(RCCS           $(projectName).qrc )      
# QT4_WRAP_UI(UIS_H  ${UIS})
# QT4_ADD_RESOURCES( RCCS_SRC  ${RCCS} )

include ( ${QT_USE_FILE} )
qt4_wrap_cpp( MOC_SOURCES ${MOC_H} )

# Don't forget to include output directory, otherwise
# the UI file won't be wrapped!
include_directories( ${CMAKE_CURRENT_BINARY_DIR}  ${CMAKE_SOURCE_DIR} )

link_directories( ${QT_LIB_DIR} )

add_executable( $(projectName) ${SRC} ${MOC_SOURCES} ${UIS_H} ${RCCS_SRC})
target_link_libraries( $(projectName) ${QT_LIBRARIES} )


install(TARGETS   $(projectName)   DESTINATION  /bin)

