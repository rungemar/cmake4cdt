/****************************************************************************
 **
 ** Copyright (C) 2008 Nokia Corporation and/or its subsidiary(-ies).
 ** Contact: Qt Software Information (qt-info@nokia.com)
 **
 ** This file is part of the example classes of the Qt Toolkit.
 **
 ** Commercial Usage
 ** Licensees holding valid Qt Commercial licenses may use this file in
 ** accordance with the Qt Commercial License Agreement provided with the
 ** Software or, alternatively, in accordance with the terms contained in
 ** a written agreement between you and Nokia.
 **
 **
 ** GNU General Public License Usage
 ** Alternatively, this file may be used under the terms of the GNU
 ** General Public License versions 2.0 or 3.0 as published by the Free
 ** Software Foundation and appearing in the file LICENSE.GPL included in
 ** the packaging of this file.  Please review the following information
 ** to ensure GNU General Public Licensing requirements will be met:
 ** http://www.fsf.org/licensing/licenses/info/GPLv2.html and
 ** http://www.gnu.org/copyleft/gpl.html.  In addition, as a special
 ** exception, Nokia gives you certain additional rights. These rights
 ** are described in the Nokia Qt GPL Exception version 1.3, included in
 ** the file GPL_EXCEPTION.txt in this package.
 **
 ** Qt for Windows(R) Licensees
 ** As a special exception, Nokia, as the sole copyright holder for Qt
 ** Designer, grants users of the Qt/Eclipse Integration plug-in the
 ** right for the Qt/Eclipse Integration to link to functionality
 ** provided by Qt Designer and its related libraries.
 **
 ** If you are unsure which license is appropriate for your use, please
 ** contact the sales department at qt-sales@nokia.com.
 **
 ****************************************************************************/

#include <QtGui>

#include "Dialog.h"

Dialog::Dialog(QWidget *parent) :
        QDialog(parent)
{

    m_gridLayout = new QGridLayout(this);

    m_label = new QLabel(this);
    m_label->setText("This is a QDialog");
    m_label->setAlignment(Qt::AlignCenter);

    m_gridLayout->addWidget(m_label, 0, 0, 1, 1);

    resize(400, 300);
    m_buttonBox = new QDialogButtonBox(this);
    m_buttonBox->setGeometry(QRect(30, 240, 341, 32));
    m_buttonBox->setOrientation(Qt::Horizontal);
    m_buttonBox->setStandardButtons(
            QDialogButtonBox::Cancel | QDialogButtonBox::Ok);

    m_gridLayout->addWidget(m_buttonBox, 1, 0, 1, 1);

    QObject::connect(m_buttonBox, SIGNAL(accepted()), this, SLOT(accept()));
    QObject::connect(m_buttonBox, SIGNAL(rejected()), this, SLOT(reject()));

    setWindowTitle(tr("Simple QDialog template"));
}

