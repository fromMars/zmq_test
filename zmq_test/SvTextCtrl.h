#pragma once
#include <wx/wxprec.h>
#ifndef WX_PRECOMP
#include <wx/wx.h>
#endif // !WX_PRECOMP

class SvTextCtrl : public wxTextCtrl
{
public:
	SvTextCtrl(wxWindow* parent);
	~SvTextCtrl();
};