
cmake_minimum_required (VERSION 2.6)

# to see the compiler output
set(CMAKE_VERBOSE_MAKEFILE ON)

project ($(projectName))


set( SOURCES main.cpp  )
             
set( HEADERS           )


## any additional defines go here 
# add_definitions(-D__STDC_CONSTANT_MACROS)

## the include search path goes here, add any additional include directories here
include_directories( ${SOURCE_DIR} )

##  the linker search path goes here, add any additional library directories here
# link_directories(  )

add_executable( $(projectName) ${SOURCES} ${HEADERS})

## to link against external librarirs, list them here:
# target_link_libraries( $(projectName) m )    # link against libm
	  
					  
install(TARGETS  $(projectName)  DESTINATION  /bin )


#
# This step merges a gdb_index section into the binary to speed up debugging.
# Doing this by add_custom_command will become obsolete as soon as the linker does it. 
#
add_custom_command(TARGET $(projectName) 
                   POST_BUILD  
                   COMMAND ARCH=${ARCH} /home/buildsys4/bin/mk-gdb-index $(projectName) 
                   WORKING_DIRECTORY ${CMAKE_CURRENT_BINARY_DIR}
                   COMMENT "building gdb index")

