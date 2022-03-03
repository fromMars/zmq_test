#include "SvTextCtrl.h"
#include <wx/settings.h>

// initializing default arguments passed to super class
SvTextCtrl::SvTextCtrl(wxWindow* parent)
	:wxTextCtrl(parent, wxID_ANY, wxEmptyString,
		wxDefaultPosition, wxDefaultSize,
		wxTE_PROCESS_ENTER | wxTE_PROCESS_TAB | wxTE_MULTILINE | 
		wxNO_BORDER | wxTE_NOHIDESEL)
{
	wxString initText;
	initText << "wxSYS_VSCROLL_X: " << 
		wxSystemSettings::GetMetric(wxSYS_VSCROLL_X) << "\r\n";
	this->AppendText(initText);	
}

SvTextCtrl::~SvTextCtrl()
{
}
