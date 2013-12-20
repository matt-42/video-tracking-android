#ifndef SPY_ANDROID_COMMON_HH_
#define SPY_ANDROID_COMMON_HH_

# include <deque>
# include <vector>
# include <cuimg/cpu/host_image2d.h>
# include <cuimg/improved_builtin.h>


double get_time();
void nv21_to_gl(int width, int height, const unsigned char* yuv, unsigned char* gl);
void gl_to_rgba(int width, int height, const unsigned char* gl, unsigned char* rgba);


struct trajectory
{
  trajectory();
  trajectory(cuimg::i_int2 pos);

  void move(trajectory&& t);
  void die();
  cuimg::i_int2 pos() const;
  int age() const;
  void set_parent_id(int id);
  cuimg::i_int2 disp(int i) const;

  cuimg::i_int2 operator[](unsigned i);

  std::deque<cuimg::i_int2> history;
  bool alive;
  int parent_id;
  int group_age;
};

template <typename TR>
void update_trajectories(std::vector<trajectory>& v, TR& pset)
{
  const auto& parts = pset.dense_particles();
  for(unsigned i = 0; i < v.size(); i++)
    if (parts[i].age > 0)
    {
      assert(parts[i].age != 1 || v[i].history.empty());
      v[i].history.push_back(parts[i].pos);
      if (v[i].history.size() > 10) v[i].history.pop_front();
    }
    else
      v[i].die();
}

#endif
