#include <ctime>

#include "common.hh"

using namespace cuimg;
double get_time()
{
  timespec ts;
  clock_gettime(0, &ts);
  return double(ts.tv_sec) + double(ts.tv_nsec) / 1000000000.;
}

void nv21_to_gl(int width, int height, const unsigned char* yuv, unsigned char* gl)
{
  memcpy(gl, yuv, width * height * sizeof(unsigned char));
}

void gl_to_rgba(int width, int height, const unsigned char* gl, unsigned char* rgba)
{
  for (int y = 0; y < height; y++) {
    for (int x = 0; x < width; x++) {
      unsigned char v = gl[y * width + x];
      rgba[(y * width + x) * 4 + 0] = v;
      rgba[(y * width + x) * 4 + 1] = v;
      rgba[(y * width + x) * 4 + 2] = v;
      rgba[(y * width + x) * 4 + 3] = 255;
    }
  }
}

trajectory::trajectory() : alive(true), parent_id(0) {}
trajectory::trajectory(i_int2 pos) : alive(true), parent_id(0) { history.push_back(pos); }

void trajectory::move(trajectory&& t)
{
  history.swap(t.history);
  alive = t.alive;
  parent_id = t.parent_id;
  group_age = t.group_age;
}

void trajectory::die()
{
  if (alive)
  {
    history.clear();
    parent_id = 0;
    alive = false;
  }
}

i_int2 trajectory::pos() const
{
  assert(age() > 0);
  return history.back();
}

int trajectory::age() const
{
  return history.size();
}

void trajectory::set_parent_id(int id)
{
  parent_id = id;
  group_age = 0;
}

i_int2 trajectory::disp(int i) const
{
  return history[age()-1] - history[age()-1-i];
}

i_int2 trajectory::operator[](unsigned i) { return history[i]; }
