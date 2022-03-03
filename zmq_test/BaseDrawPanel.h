#pragma once
#include <wx/wxprec.h>
#ifndef WX_PRECOMP
#include <wx/wx.h>
#endif // !WX_PRECOMP

#include <wx/sizer.h>
#include <wx/datetime.h>
#include <wx/string.h>
#include <wx/panel.h>

// class define
class BaseDrawPanel : public wxPanel
{
public:
	BaseDrawPanel(wxFrame* parent);
	DECLARE_EVENT_TABLE()
private:
	void render(wxPaintEvent& evt);
	void mouseMoved(wxMouseEvent& event);
	void mouseDown(wxMouseEvent& event);
	void mouseWheelMoved(wxMouseEvent& event);
	void mouseRelease(wxMouseEvent& event);
	void rightClick(wxMouseEvent& event);
	void mouseLeftWindow(wxMouseEvent& event);
	void keyPressed(wxMouseEvent& event);
	void keyReleased(wxMouseEvent& event);
	void onRender(wxCommandEvent& evt);
};